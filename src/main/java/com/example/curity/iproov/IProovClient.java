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

package com.example.curity.iproov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.http.HttpRequest;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.example.curity.iproov.IProovAuthenticationActionConstants.iProovEndpoints.*;

public class IProovClient {
    private final static Logger _logger = LoggerFactory.getLogger(IProovClient.class);
    private final HttpClient _client;
    private final Json _json;
    private final String _iproovTenant;
    private final String _iproovBaseURL;
    private final String _iproovApiKey;
    private final String _iproovApiSecret;

    public IProovClient(
            HttpClient client,
            String iproovTenant,
            String iproovBaseURL,
            String iproovApiKey,
            String iproovApiSecret,
            Json json)
    {
        _client = client;
        _iproovTenant = iproovTenant;
        _iproovBaseURL = iproovBaseURL;
        _iproovApiKey = iproovApiKey;
        _iproovApiSecret = iproovApiSecret;
        _json = json;
    }

    public String getIProovTenant()
    {
        return _iproovTenant;
    }

    private HttpResponse getTokenResponse(String subject, String tokenEndpoint, String resource)
    {
        URI uri = URI.create("https://" + _iproovTenant + _iproovBaseURL + tokenEndpoint);

        HttpResponse response = _client
                .request(uri)
                .body(HttpRequest.fromJson(createTokenData(
                        resource,
                        subject), _json))
                .timeout(Duration.ofSeconds(10))
                .contentType("application/json")
                .post()
                .response();

        int statusCode = response.statusCode();

        if(statusCode == 200)
        {
            Map<String, Object> body = response.body(HttpResponse.asJsonObject(_json));
            _logger.debug("iProov token response successful");
        }
        else
        {
            _logger.debug("iProov token response returned status code" + response.statusCode());
        }

        return response;
    }

    public HttpResponse getEnrolTokenResponse(String subject)
    {
        _logger.debug("Getting iProov enrol token");
        return getTokenResponse(subject, ENROLL_TOKEN, "onboarding");
    }

    public HttpResponse getVerifyTokenResponse(String subject)
    {
        _logger.debug("Getting iProov verify token");
        return getTokenResponse(subject, VERIFY_TOKEN, "login");
    }

    public boolean validateUser(String subject, String userAgent, String token)
    {
        _logger.debug("Validating iProov user");
        URI uri = URI.create("https://" + _iproovTenant + _iproovBaseURL + ENROL_VALIDATE);

        HttpResponse iProovValidateResponse = _client
                .request(uri)
                .body(HttpRequest.fromJson(createUserValidationData(
                        subject,
                        userAgent,
                        token), _json))
                .timeout(Duration.ofSeconds(10))
                .contentType("application/json")
                .post()
                .response();

        int statusCode = iProovValidateResponse.statusCode();

        if (statusCode == 200)
        {
            _logger.debug("iProov user validated successful");
            return true;
        }

        return false;
    }

    public Map<String, Object> verifyUser(String subject, String userAgent, String token)
    {
        _logger.debug("Verifying iProov user with token " + token);
        URI uri = URI.create("https://" + _iproovTenant + _iproovBaseURL + VALIDATE_USER);

        HttpResponse iProovVerifyResponse = _client
                .request(uri)
                .body(HttpRequest.fromJson(createVerifyUserData(
                        subject,
                        userAgent,
                        token), _json))
                .timeout(Duration.ofSeconds(10))
                .contentType("application/json")
                .post()
                .response();

        int statusCode = iProovVerifyResponse.statusCode();

        if (statusCode == 200)
        {
            _logger.debug("iProov user verification successful");
        }

        return iProovVerifyResponse.body(HttpResponse.asJsonObject(_json));
    }

    private Map<String, String> createTokenData(String resource, String subject)
    {
        _logger.debug("Creating iProov token data");
        Map<String, String> data = new HashMap<>(5);
        data.put("api_key", _iproovApiKey);
        data.put("secret", _iproovApiSecret);
        data.put("resource", resource);
        data.put("assurance_type", "genuine_presence");
        data.put("user_id", subject);
        return data;
    }

    private Map<String, Object> createUserValidationData(String subject, String userAgent, String token)
    {
        _logger.debug("Creating iProov user validation data");
        Map<String, Object> data = new HashMap<>(6);
        data.put("api_key", _iproovApiKey);
        data.put("secret", _iproovApiSecret);
        data.put("token", token);
        data.put("user_id", subject);
        data.put("client", userAgent);
        data.put("activate", true);
        return data;
    }

    private Map<String, String> createVerifyUserData(String subject, String userAgent, String token)
    {
        _logger.debug("Creating iProov user verification data");
        Map<String, String> data = new HashMap<>(5);
        data.put("api_key", _iproovApiKey);
        data.put("secret", _iproovApiSecret);
        data.put("token", token);
        data.put("user_id", subject);
        data.put("client", userAgent);
        return data;
    }
}
