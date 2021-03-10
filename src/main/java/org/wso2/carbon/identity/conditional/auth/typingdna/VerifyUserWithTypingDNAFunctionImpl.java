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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.identity.application.authentication.framework.AsyncProcess;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.JsGraphBuilder;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.conditional.auth.functions.common.utils.CommonUtils;

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

import static org.wso2.carbon.identity.conditional.auth.functions.common.utils.Constants.OUTCOME_FAIL;
import static org.wso2.carbon.identity.conditional.auth.functions.common.utils.Constants.OUTCOME_SUCCESS;

/**
 * Custom adaptive function implementation for verifying
 * users typing pattern with TypingDNA.
 */

public class VerifyUserWithTypingDNAFunctionImpl implements VerifyUserWithTypingDNAFunction {

    private static final Log log = LogFactory.getLog(VerifyUserWithTypingDNAFunctionImpl.class);

    @Override
    public void verifyUserWithTypingDNA(JsAuthenticationContext context, Map<String, Object> eventHandlers) throws Exception {

        JsAuthenticatedUser user = utils.getUser(context);
        String username = user.getWrapped().getUserName();
        String tenantDomain = user.getWrapped().getTenantDomain();
        String typingPattern = utils.getTypingPattern(context);

        AtomicBoolean result = new AtomicBoolean(false);

        //Getting connector configurations.
        String APIKey = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.USERNAME, tenantDomain);
        String APISecret = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.CREDENTIAL, tenantDomain);
        String advanced = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.ADVANCE_MODE_ENABLED, tenantDomain);
        String region = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.REGION, tenantDomain);
        String Enabled = CommonUtils.getConnectorConfig(TypingDNAConfigImpl.ENABLE, tenantDomain);
        String userID = getUserID(username, tenantDomain);

        //Selecting the suitable typingDNA API according to the configuration.
        String api = advanced.equals(Constants.TRUE) ? Constants.VERIFY_API : Constants.AUTO_API;

        AsyncProcess asyncProcess = new AsyncProcess((authenticationContext, asyncReturn) -> {
            try {
                if (!(typingPattern == null) && !typingPattern.equals(Constants.NULL) && Enabled.equals(Constants.TRUE)) {

                    String baseurl = buildURL(region, api, userID);
                    String data = "tp=" + URLEncoder.encode(typingPattern, "UTF-8");
                    String Authorization = Base64.getEncoder().encodeToString((APIKey + ":" + APISecret).getBytes(StandardCharsets.UTF_8));

                    //Setting up URL connection.
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

                    //Reading the response.
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    StringBuilder res = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        res.append(line);
                        res.append('\r');
                    }
                    rd.close();

                    log.debug(res.toString());

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
                    if (eCode.equals(1L) && advanced.equals(Constants.TRUE)) {
                        map.put(Constants.SCORE, (Long) apiResponse.get(Constants.SCORE));
                        map.put(Constants.CONFIDENCE, (Long) apiResponse.get(Constants.CONFIDENCE));
                        map.put(Constants.COMPARED_PATTERNS, (Long) apiResponse.get(Constants.COMPARED_SAMPLES));
                    }
                    asyncReturn.accept(authenticationContext, map, OUTCOME_SUCCESS);

                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(Constants.TYPING_PATTERN_RECEIVED, false);
                    asyncReturn.accept(authenticationContext, map, OUTCOME_SUCCESS);
                }

            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
                log.debug("Error while connecting to TypingDNA APIs");
                asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);

            } catch (IOException e) {
                log.error(e.getMessage(), e);
                log.debug("Error in TypingDNA Configuration");
                asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);

            } catch (ParseException e) {
                log.error(e.getMessage(), e);
                asyncReturn.accept(authenticationContext, Collections.emptyMap(), OUTCOME_FAIL);
            }

        });
        JsGraphBuilder.addLongWaitProcess(asyncProcess, eventHandlers);
    }

    /**
     * Method to create the request url with configured region,api and userID.
     */
    private String buildURL(String region, String api, String userID) {

        return "https://api-" + region + ".typingdna.com/" + api + "/" + userID;
    }

    /**
     * Method to create userID used in TypingDNA API call.
     */
    private String getUserID(String username, String tenantDomain) {

        return DigestUtils.sha256Hex(username + "@" + tenantDomain);
    }

}
