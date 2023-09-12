/*
 *  Copyright 2023 Curity AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.curity.iproov.handlers;

import com.example.curity.iproov.IProovClient;
import com.example.curity.iproov.config.IProovAuthenticationActionConfig;
import com.example.curity.iproov.models.ScanRequestModel;
import com.example.curity.iproov.models.ScannedDocument;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionRequestHandler;
import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionResult;
import se.curity.identityserver.sdk.authenticationaction.completions.IntermediateAuthenticationState;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static com.example.curity.iproov.IProovAuthenticationActionConstants.FormValueNames.*;
import static com.example.curity.iproov.IProovAuthenticationActionConstants.SessionKeys.SCANNED_DOCUMENT;
import static com.example.curity.iproov.IProovAuthenticationActionConstants.SessionKeys.SESSION_KEY;
import static com.example.curity.iproov.IProovAuthenticationActionConstants.SubjectAttributes.SCAN_ATTRIBUTES;
import static se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionResult.complete;
import static se.curity.identityserver.sdk.http.HttpStatus.ACCEPTED;
import static se.curity.identityserver.sdk.web.Response.ResponseModelScope.ANY;


public class IProovAuthenticationActionRequestHandler implements ActionCompletionRequestHandler<ScanRequestModel>
{
    private final static Logger _logger = LoggerFactory.getLogger(IProovAuthenticationActionRequestHandler.class);
    private final SessionManager _sessionManager;
    private final Json _json;
    private static final Gson gson = new Gson();
    private final IntermediateAuthenticationState _authState;
    private final IProovClient _iProovClient;
    private final String _subject;
    private Boolean _isEnrolled = false;

    public IProovAuthenticationActionRequestHandler(IntermediateAuthenticationState intermediateAuthenticationState, IProovAuthenticationActionConfig configuration)
    {
        _sessionManager = configuration.getSessionManager();
        _json = configuration.getJson();
        _authState = intermediateAuthenticationState;
        _subject = _authState.getAuthenticationAttributes().getSubject();
        _iProovClient = new IProovClient(
                configuration.getHttpClient(),
                configuration.getIproovTenant(),
                configuration.getIproovBaseURL(),
                configuration.getIproovApiKey(),
                configuration.getIproovApiSecret(),
                _json);
    }

    @Override
    public ScanRequestModel preProcess(Request request, Response response)
    {
        String token = null;
        String _urlPath = null;

        try{
            _urlPath = new URL(request.getUrl()).getPath();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpResponse enrolTokenResponse = _iProovClient.getEnrolTokenResponse(_subject);

        Map<String, Object> responseMap = _json.fromJson(enrolTokenResponse.body(HttpResponse.asString()));

        Map<String, Object> enrolResponseMap = _json.fromJson(enrolTokenResponse.body(HttpResponse.asString()));

        //If a token is returned from enroll token API then the user is not enrolled yet
        if(enrolResponseMap.containsKey("token"))
        {
            token = enrolResponseMap.get("token").toString();
        }
        else
        {
            //No enroll token, user is already enrolled obtain verify token
            _isEnrolled = true;
            HttpResponse verifyTokenResponse = _iProovClient.getVerifyTokenResponse(_subject);
            Map<String, Object> verifyResponseMap = _json.fromJson(verifyTokenResponse.body(HttpResponse.asString()));
            token = verifyResponseMap.get("token").toString();
        }
        response.setResponseModel(ResponseModel.templateResponseModel(Map.of(
                        IPROOV_SCAN_URL, _urlPath,
                        IPROOV_BASE_URL, "https://" + _iProovClient.getIProovTenant(),
                        IPROOV_TOKEN, token),
                "iproov/get"), ANY);

        return new ScanRequestModel(request, _json);
    }

    @Override
    public Optional<ActionCompletionResult> get(ScanRequestModel request, Response response)
    {
        return Optional.empty();
    }

    @Override
    public Optional<ActionCompletionResult> post(ScanRequestModel scanRequestModel, Response response)
    {
        if (_isEnrolled && scanRequestModel.getPostRequestModel().isPollingDone())
        {
            String scanAttributes = Optional.ofNullable(_sessionManager.get(SCANNED_DOCUMENT))
                    .map(attribute -> attribute.getOptionalValueOfType(String.class))
                    .orElse("");

            String responseToken = gson.fromJson(scanAttributes, ScannedDocument.class).getToken();

            _sessionManager.put(Attribute.of(SESSION_KEY, true));

            Map<String, Object> verifyAttributes = _iProovClient.verifyUser(
                    _authState.getAuthenticationAttributes().getSubject(),
                    scanRequestModel.getPostRequestModel().getUserAgent(),
                    responseToken);

            if(verifyAttributes != null)
            {
                _logger.debug("User successfully verified using iProov");
                _sessionManager.put(Attribute.of(SCAN_ATTRIBUTES,  gson.toJson(verifyAttributes)));
                return Optional.of(complete());
            }
        }
        else if(!_isEnrolled)
        {
            String scanAttributes = Optional.ofNullable(_sessionManager.get(SCANNED_DOCUMENT))
                    .map(attribute -> attribute.getOptionalValueOfType(String.class))
                    .orElse("");

            String responseToken = gson.fromJson(scanAttributes, ScannedDocument.class).getToken();

            Boolean isValidated = _iProovClient.validateUser(
                    _authState.getAuthenticationAttributes().getSubject(),
                    scanRequestModel.getPostRequestModel().getUserAgent(),
                    responseToken);

            if(isValidated)
            {
                _logger.debug("User successfully enrolled and validated using iProov");
                _sessionManager.put(Attribute.of(SCAN_ATTRIBUTES,  isValidated));
                return Optional.of(complete());
            }
        }
        else
        {
            response.setHttpStatus(ACCEPTED); // stop polling
        }

        @Nullable Map<String, Object> scannedAttributes = scanRequestModel.getPostRequestModel().getAttributes();

        _sessionManager.put(Attribute.of(SCANNED_DOCUMENT,  gson.toJson(scannedAttributes)));

        return Optional.empty();
    }
}