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

public class ScannedDocument {
    String feedback;
    String reason;
    Boolean is_native_bridge;
    Boolean passed;
    String token;
    String type;

    public String getFeedback() { return feedback; }
    public String getReason() { return reason; }
    public Boolean getIsNativeBridge() { return is_native_bridge; }
    public Boolean getPassed() { return passed; }
    public String getToken() { return token; }
    public String getType() { return type; }
}
