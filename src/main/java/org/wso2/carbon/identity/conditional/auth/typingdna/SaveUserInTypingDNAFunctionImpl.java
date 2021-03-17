
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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.conditional.auth.functions.common.utils.CommonUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

/**
 * Custom adaptive function implementation for Save
 * users typing pattern in TypingDNA.
 */
public class SaveUserInTypingDNAFunctionImpl implements SaveUserInTypingDNAFunction {

    private static final Log log = LogFactory.getLog(VerifyUserWithTypingDNAFunctionImpl.class);

    @Override
    public void saveUserInTypingDNA(JsAuthenticationContext context) throws Exception {

        JsAuthenticatedUser user = utils.getUser(context);
        String username = user.getWrapped().getUserName();
        String tenantDomain = user.getWrapped().getTenantDomain();
        String typingPattern = utils.getTypingPattern(context);

        // Getting connector configurations.
        String APIKey = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.USERNAME, tenantDomain);
        String APISecret = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.CREDENTIAL, tenantDomain);
        String advanced = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.ADVANCE_MODE_ENABLED, tenantDomain);
        String region = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.REGION, tenantDomain);

        String userID = getUserID(username, tenantDomain);

        if (!(typingPattern == null) && !typingPattern.equals(Constants.NULL)) {
            String baseurl = buildURL(region, userID);
            String data = "tp=" + URLEncoder.encode(typingPattern, "UTF-8");
            String Authorization = Base64.getEncoder().encodeToString((APIKey + ":" + APISecret).getBytes(StandardCharsets.UTF_8));

            // Setting up URL connection.
            URL url = new URL(baseurl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + Authorization);

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(data);
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder res = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                res.append(line);
                res.append('\r');
            }
            rd.close();

            // Response from TypingDNA
            log.debug(res.toString());

        }

    }

    private String buildURL(String region, String userID) {

        return "https://api-" + region + ".typingdna.com/save" + "/" + userID;
    }

    private String getUserID(String username, String tenantDomain) {

        return DigestUtils.sha256Hex(username + "@" + tenantDomain);
    }
}
