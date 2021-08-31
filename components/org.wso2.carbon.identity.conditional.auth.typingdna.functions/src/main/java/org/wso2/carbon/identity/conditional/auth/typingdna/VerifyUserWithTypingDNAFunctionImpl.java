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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.identity.application.authentication.framework.AsyncProcess;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsGraphBuilder;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

import static org.wso2.carbon.identity.conditional.auth.functions.common.utils.Constants.OUTCOME_FAIL;
import static org.wso2.carbon.identity.conditional.auth.functions.common.utils.Constants.OUTCOME_SUCCESS;

/**
 * Custom adaptive function implementation for verifying
 * users typing pattern with TypingDNA.
 */
public class VerifyUserWithTypingDNAFunctionImpl implements VerifyUserWithTypingDNAFunction {

    private static final Log log = LogFactory.getLog(VerifyUserWithTypingDNAFunctionImpl.class);

    /**
     * Function to send verify request to typingDNA APIs.
     *
     * @param context       Context from authentication flow.
     * @param eventHandlers Defines the flow of process in onSuccess & onFail.
     * @throws TypingDNAAuthenticatorException When unable to retrieve tenant configurations.
     */
    @Override
    public void verifyUserWithTypingDNA(JsAuthenticationContext context, Map<String, Object> eventHandlers) throws TypingDNAAuthenticatorException {

        try {
            JsAuthenticatedUser user = Utils.getUser(context);
            String username = user.getWrapped().getUserName();
            String tenantDomain = user.getWrapped().getTenantDomain();
            String typingPattern = Utils.getTypingPattern(context);

            AtomicBoolean result = new AtomicBoolean(false);

            // Getting connector configurations.
            String APIKey = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.USERNAME, tenantDomain);
            String APISecret = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.CREDENTIAL, tenantDomain);
            boolean isAdvanceModeEnabled = Boolean.parseBoolean(CommonUtils.getConnectorConfig(TypingDNAConfigImpl.ADVANCE_MODE_ENABLED, tenantDomain));
            String region = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.REGION, tenantDomain);
            boolean isTypingDNAEnabled = Boolean.parseBoolean(CommonUtils.getConnectorConfig(TypingDNAConfigImpl.ENABLE, tenantDomain));

            String userID = getUserID(username, tenantDomain);

            // Selecting the suitable typingDNA API according to the configuration.
            String api = isAdvanceModeEnabled ? Constants.VERIFY_API : Constants.AUTO_API;

            AsyncProcess asyncProcess = new AsyncProcess((authenticationContext, asyncReturn) -> {
                try {
                    if (StringUtils.isNotBlank(typingPattern) && !StringUtils.equalsIgnoreCase(Constants.NULL, typingPattern)
                            && isTypingDNAEnabled) {

                        String baseurl = buildURL(region, api, userID);
                        String data = "tp=" + URLEncoder.encode(typingPattern, "UTF-8") + "&custom_field=" + URLEncoder
                                .encode(Constants.CUSTOM_FIELD_VALUE, "UTF-8");
                        String Authorization = Base64.getEncoder().encodeToString((APIKey + ":" + APISecret).getBytes(StandardCharsets.UTF_8));

                        // Setting up URL connection.
                        URL url = new URL(baseurl);
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setRequestProperty("Authorization", "Basic " + Authorization);

                        connection.setUseCaches(false);
                        connection.setDoOutput(true);

                        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                        wr.writeBytes(data);
                        wr.close();

                        // Reading the response.
                        InputStream is = connection.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        StringBuilder res = new StringBuilder();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            res.append(line);
                            res.append('\r');
                        }
                        rd.close();

                        // Response from TypingDNA.
                        if (log.isDebugEnabled()) {
                            log.debug("Response from TypingDNA for the user: " + username + ":  " + res.toString());
                        }

                        JSONParser parser = new JSONParser();
                        JSONObject apiResponse = (JSONObject) parser.parse(res.toString());
                        Long eCode = (Long) apiResponse.get(Constants.MESSAGE_CODE);

                        if (eCode.equals(1L)) {
                            Long typingResult = (Long) apiResponse.get(Constants.RESULT);
                            result.set(typingResult.equals(1L));
                        }
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(Constants.RESULT, result.get());
                        map.put(Constants.TYPING_PATTERN_RECEIVED, true);
                        if (eCode.equals(1L) && isAdvanceModeEnabled) {
                            map.put(Constants.SCORE, apiResponse.get(Constants.SCORE));
                            map.put(Constants.CONFIDENCE, apiResponse.get(Constants.CONFIDENCE));
                            map.put(Constants.COMPARED_PATTERNS, apiResponse.get(Constants.COMPARED_SAMPLES));
                        }
                        asyncReturn.accept(authenticationContext, map, OUTCOME_SUCCESS);

                    } else if (StringUtils.equalsIgnoreCase(Constants.NULL, typingPattern)) {
                        if (log.isDebugEnabled()) {
                            log.debug("User: " + username + " is prompted to second step due to using password manager.");
                        }
                        asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);

                    } else {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(Constants.TYPING_PATTERN_RECEIVED, false);
                        asyncReturn.accept(authenticationContext, map, OUTCOME_SUCCESS);
                    }

                } catch (UnknownHostException e) {
                    log.error(e.getMessage(), e);
                    if (log.isDebugEnabled()) {
                        log.debug("Error while connecting to TypingDNA APIs.");
                    }
                    asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);

                } catch (SSLException e) {
                    log.error(e.getMessage(), e);
                    if (log.isDebugEnabled()) {
                        log.debug("Can not connect the user: " + username + " to TypingDNA.");
                    }
                    asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);

                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    if (log.isDebugEnabled()) {
                        log.debug("Error in TypingDNA Configuration in " + tenantDomain + " tenant.");
                    }
                    asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);

                } catch (ParseException e) {
                    log.error(e.getMessage(), e);
                    asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);
                }
            });
            JsGraphBuilder.addLongWaitProcess(asyncProcess, eventHandlers);
        } catch (IdentityEventException e) {
            throw new TypingDNAAuthenticatorException("Can not retrieve configurations from tenant.", e);
        }
    }

    /**
     * This function builds the URL that is used to make API calls
     *
     * @param region TypingDNA API region - eu/us.
     * @param api    API that going to be called - auto/verify.
     * @param userID Unique identifier of a user in TypingDNA.
     * @return URL that is used to call typingDNA API.
     */
    private String buildURL(String region, String api, String userID) {

        return "https://api-" + region + ".typingdna.com/" + api + "/" + userID;
    }

    /**
     * This function generates a unique identifier for users in TypingDNA.
     *
     * @param username     Name of the user.
     * @param tenantDomain Name of the tenant domain.
     * @return Hashed value of tenant qualified username.
     */
    private String getUserID(String username, String tenantDomain) {

        return DigestUtils.sha256Hex(username + "@" + tenantDomain);
    }

}
