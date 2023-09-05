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

import com.example.curity.iproov.config.IProovAuthenticationActionConfig;

import com.google.common.collect.ImmutableMap;
import com.google.common.html.HtmlEscapers;

import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionRequestHandler;
import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionResult;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.SessionManager;

import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static com.example.curity.iproov.IProovAuthenticationActionConstants.FormValueNames.*;
import static com.example.curity.iproov.IProovAuthenticationActionConstants.SessionKeys.SESSION_KEY;
import static com.example.curity.iproov.Utils.cleanup;
import static com.example.curity.iproov.Utils.stripLastPathPart;
import static se.curity.identityserver.sdk.http.HttpStatus.OK;
import static se.curity.identityserver.sdk.web.Response.ResponseModelScope.NOT_FAILURE;
import static se.curity.identityserver.sdk.web.ResponseModel.mapResponseModel;
import static se.curity.identityserver.sdk.web.ResponseModel.templateResponseModel;

public class IProovAuthenticationActionFailedRequestHandler implements ActionCompletionRequestHandler<Request> {
    private final ExceptionFactory _exceptionFactory;

    private final SessionManager _sessionManager;

    public IProovAuthenticationActionFailedRequestHandler(IProovAuthenticationActionConfig config)
    {
        _exceptionFactory = config.getExceptionFactory();
        _sessionManager = config.getSessionManager();
    }

    @Override
    public Request preProcess(Request request, Response response)
    {
        response.setResponseModel(templateResponseModel(ImmutableMap.of(),
                "failed/index"), NOT_FAILURE);
        return request;
    }

    @Override
    public Optional<ActionCompletionResult> get(Request request, Response response)
    {
        try {
            response.setResponseModel(mapResponseModel(Map.of(
                    ERROR_MESSAGE, HtmlEscapers.htmlEscaper().escape("An error occurred"),
                    RESTART_URL, stripLastPathPart(request.getUrl())
            )), OK);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        finally {
            cleanup(_sessionManager);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ActionCompletionResult> post(Request request, Response response)
    {
        _sessionManager.remove(SESSION_KEY);

        String _urlPath;
        try {
            _urlPath = new URL(request.getUrl()).getPath();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        throw _exceptionFactory.redirectException(_urlPath);
//        throw _exceptionFactory.methodNotAllowed("HTTP POST not supported for this url.");
    }
}
