/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.conditional.auth.typingdna;

/**
 * Constants used in verifyUserWithTypingDNA, saveUserInTypingDNA functions.
 */
public abstract class Constants {

    public static final String RESULT = "result";
    public static final String SCORE = "score";
    public static final String CONFIDENCE = "confidence";
    public static final String MESSAGE_CODE = "message_code";
    public static final String COMPARED_SAMPLES = "compared_samples";
    public static final String VERIFY_API = "verify";
    public static final String AUTO_API = "auto";
    public static final String TRUE = "true";
    public static final String NULL = "null";
    public static final String TYPING_PATTERN_RECEIVED = "isTypingPatternReceived";
    public static final String COMPARED_PATTERNS = "comparedPatterns";
}
