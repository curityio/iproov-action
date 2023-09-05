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

public final class IProovAuthenticationActionConstants {

    public static class Endpoints
    {
        public static final String INDEX = "index";
        public static final String CANCEL = "cancel";
        public static final String FAILED = "failed";
    }

    public static class FormValueNames
    {
        public static final String CANCEL_URL = "_cancelUrl";
        public static final String FAIL_URL = "_failUrl";
        public static final String RESTART_URL = "_restartUrl";
        public static final String IPROOV_SCAN_URL = "_iproovScanUrl";
        public static final String IPROOV_BASE_URL = "_iproovBaseUrl";
        public static final String IPROOV_TOKEN = "_iproovEnrolToken";
        public static final String POLLING_DONE = "_pollingDone";
        public static final String ERROR_MESSAGE = "_errorMessage";
    }

    public static class SessionKeys
    {
        public static final String SCANNED_DOCUMENT = "scannedDocument";
        public final static String SESSION_KEY = "IPROOV";
    }

    public static class iProovEndpoints
    {
        public static final String ENROLL_TOKEN = "/claim/enrol/token";
        public static final String VERIFY_TOKEN = "/claim/verify/token";
        public static final String ENROL_VALIDATE = "/claim/enrol/validate";
        public static final String VALIDATE_USER = "/claim/verify/validate";
    }

    public static class SubjectAttributes
    {
        public static final String SCAN_ATTRIBUTES = "iproov_user_attributes";

    }

    public static class MessageKeys
    {
        public static final String USER_CANCELLED = "user.cancelled";
    }
}
