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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import se.curity.identityserver.sdk.web.Request;

import javax.annotation.Nullable;
import java.util.Optional;

public final class FailedRequestModel
{
    @Nullable
    @Valid
    private static Get _getRequestModel;

    public FailedRequestModel(Request request)
    {
        _getRequestModel = request.isGetRequest() ? new Get(request) : null;
    }

    public static Get getGetRequestModel()
    {
        return Optional.ofNullable(_getRequestModel).orElseThrow(() ->
                new RuntimeException("Get RequestModel does not exist"));
    }

    public static final class Get
    {
        private final String _incomingErrorMessage;

        @Nullable
        private final String _errorMessage;

        Get(Request request)
        {
            var listOfMessages = request.getQueryParameterValues("_errorMessage");

            if (listOfMessages != null && listOfMessages.size() > 0)
            {
                if (listOfMessages.size() > 1)
                {
                    throw new RuntimeException("Invalid Param");
                }
                _incomingErrorMessage = listOfMessages.stream().findFirst().orElse("unknown");
            }
            else
            {
                _incomingErrorMessage = "unknown";
            }

            if (_incomingErrorMessage.equals("unknown"))
            {

                _errorMessage = "unknown";
            }
            else
            {
                _errorMessage = null;
            }
        }

        @NotEmpty
        public String getErrorMessage()
        {
            @Nullable String errorMessage = _incomingErrorMessage.equals("unknown") ? _errorMessage : _incomingErrorMessage;

            return errorMessage == null || "[object Object]".equals(errorMessage) ? "unknown" : errorMessage;
        }
    }
}
