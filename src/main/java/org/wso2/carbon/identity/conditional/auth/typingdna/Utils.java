
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

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsGraphBuilder;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsParameters;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * class that implements methods to extract User details and
 * typing patterns from the context
 */
public class Utils {

    public static JsAuthenticatedUser getUser(JsAuthenticationContext context) {

        return (JsAuthenticatedUser) context.getMember("currentKnownSubject");
    }

    public static String getTypingPattern(JsAuthenticationContext context) {

        JsServletRequest request = (JsServletRequest) context.getMember("request");
        JsParameters params = (JsParameters) request.getMember("params");
        String[] arr = (String[]) params.getWrapped().get("typingPattern");
        if (arr != null) {
            return arr[0];
        } else {
            return null;
        }
    }

    /**
     * Unused method. Showing an Error page if admin configured credentials wrong.
     */

    public static void showError() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", "Unauthorized");
        map.put("statusMsg", "Error in typingDNA Configuration");
        JsGraphBuilder.sendErrorAsync("", map);

    }
}
