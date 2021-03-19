
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.conditional.auth.functions.common.utils.CommonUtils;
import org.wso2.carbon.identity.conditional.auth.typingdna.exception.TypingDNAAuthenticatorException;
import org.wso2.carbon.identity.event.IdentityEventException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

/**
 * Custom adaptive function implementation for Save
 * users typing pattern in TypingDNA.
 */
public class SaveUserInTypingDNAFunctionImpl implements SaveUserInTypingDNAFunction {

    private static final Log log = LogFactory.getLog(VerifyUserWithTypingDNAFunctionImpl.class);

    /**
     * Function to send save request to typingDNA APIs.
     *
     * @param context Context from authentication flow.
     * @throws Exception
     */
    @Override
    public void saveUserInTypingDNA(JsAuthenticationContext context) throws TypingDNAAuthenticatorException {

        try {
            JsAuthenticatedUser user = Utils.getUser(context);
            String username = user.getWrapped().getUserName();
            String tenantDomain = user.getWrapped().getTenantDomain();
            String typingPattern = Utils.getTypingPattern(context);

            // Getting connector configurations.
            String APIKey = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.USERNAME, tenantDomain);
            String APISecret = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.CREDENTIAL, tenantDomain);
            String advanced = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.ADVANCE_MODE_ENABLED, tenantDomain);
            String region = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.REGION, tenantDomain);
            String Enabled = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.ENABLE, tenantDomain);

            String userID = getUserID(username, tenantDomain);

            if (StringUtils.isNotBlank(typingPattern) && !StringUtils.equalsIgnoreCase(Constants.NULL, typingPattern)
                    && Enabled.equals(Constants.TRUE)) {
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
                if (log.isDebugEnabled()) {
                    log.debug("Response from TypingDNA: " + res.toString());
                }

            }
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            if (log.isDebugEnabled()) {
                log.debug("Error while connecting to TypingDNA APIs.");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            if (log.isDebugEnabled()) {
                log.debug("Error in TypingDNA Configuration");
            }
        } catch (IdentityEventException e) {
            throw new TypingDNAAuthenticatorException("Can not retrieve configurations from tenant.", e);
        }

    }

    /**
     * This function builds the URL that is used to make API calls
     *
     * @param region TypingDNA API region - eu/us.
     * @param userID Unique identifier of a user in TypingDNA.
     * @return URL that is used to call typingDNA API.
     */
    private String buildURL(String region, String userID) {

        return "https://api-" + region + ".typingdna.com/save" + "/" + userID;
    }

    /**
     * This function generates a unique identifier for users in TypingDNA.
     *
     * @param username     Name of the user.
     * @param tenantDomain Name of the tenant domain.
     * @return Hashed value fully tenant qualified username.
     */
    private String getUserID(String username, String tenantDomain) {

        return DigestUtils.sha256Hex(username + "@" + tenantDomain);
    }
}
