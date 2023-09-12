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

package com.example.curity.iproov.models;

import com.google.gson.Gson;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.web.Request;

import java.util.Map;
import java.util.Optional;

import static com.example.curity.iproov.IProovAuthenticationActionConstants.FormValueNames.POLLING_DONE;

public final class ScanRequestModel
{
    @Nullable
    @Valid
    private final Post _postRequestModel;
    private static final Gson gson = new Gson();
    private static String _userAgent;

    public ScanRequestModel(Request request, Json json)
    {
        _postRequestModel = request.isPostRequest() ? new Post(request, json) : null;
        _userAgent = request.getHeaders().firstValue("User-Agent");
    }
    public Post getPostRequestModel()
    {
        return Optional.ofNullable(_postRequestModel).orElseThrow(() ->
                new RuntimeException("Post RequestModel does not exist"));
    }

    public static class Post
    {
        private final boolean _isPollingDone;
        @Nullable
        private Map<String, Object> _attributes;
        @Nullable
        private ScannedDocument _scannedDocument;

        Post(Request request, Json json)
        {
            if (!request.getBodyAsString().contains(POLLING_DONE))
            {
                _attributes = json.fromJson(request.getBodyAsString());
                _scannedDocument = gson.fromJson(request.getBodyAsString(), ScannedDocument.class);
            }

            _isPollingDone = Boolean.parseBoolean(request.getFormParameterValueOrError(POLLING_DONE));
        }

        @Nullable
        public Map<String, Object> getAttributes()
        {
            return _attributes;
        }
        @Nullable
        public ScannedDocument getScannedDocument()
        {
            return _scannedDocument;
        }
        public boolean isPollingDone()
        {
            return _isPollingDone;
        }
        public String getUserAgent()
        {
            return _userAgent;
        }

    }
}
