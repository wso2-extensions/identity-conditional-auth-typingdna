package org.wso2.carbon.identity.conditional.auth.typingdna.api.core;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.conditional.auth.functions.common.utils.CommonUtils;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.dto.TypingdnaEnableResponseDTO;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.event.IdentityEventException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

public class TypingDNAServiceImpl {

    private static final Log log = LogFactory.getLog(TypingDNAServiceImpl.class);

    /**
     * This function is used to delete users' typing patterns that are
     * saved in typingDNA.
     *
     * @throws IOException When error/mistakes in TypingDNA configuration for tenant.
     */
    public void deleteTypingPatterns() throws IOException {

        // Getting username & tenant domain & generating userID.
        String username = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
        String tenantDomain = "carbon.super";
        if (IdentityUtil.threadLocalProperties.get().get("TenantNameFromContext") != null) {
            tenantDomain = (String) IdentityUtil.threadLocalProperties.get().get("TenantNameFromContext");
        }
        String userID = getUserID(username, tenantDomain);

        try {

            String APIKey = CommonUtils.getConnectorConfig("adaptive_authentication.tdna.username", tenantDomain);
            String APISecret = CommonUtils.getConnectorConfig("__secret__adaptive_authentication.tdna.password", tenantDomain);
            String region = CommonUtils.getConnectorConfig("adaptive_authentication.tdna.apiregion", tenantDomain);

            String baseurl = buildURL(region, userID);
            String Authorization = Base64.getEncoder().encodeToString((APIKey + ":" + APISecret).getBytes(StandardCharsets.UTF_8));
            URL url = new URL(baseurl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + Authorization);

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder res = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                res.append(line);
                res.append('\r');
            }
            rd.close();
        } catch (MalformedInputException e) {
            log.error(e);
        } catch (IdentityEventException e) {
            log.error(e);
        }

    }

    /**
     * Function that is used to get whether TypingDNA is Enabled or not for the tenant.
     *
     * @return Response that contains a boolean value Enabled-True , else - False.
     */
    public TypingdnaEnableResponseDTO getEnabled() {

        String tenantDomain = "carbon.super";
        if (IdentityUtil.threadLocalProperties.get().get("TenantNameFromContext") != null) {
            tenantDomain = (String) IdentityUtil.threadLocalProperties.get().get("TenantNameFromContext");
        }
        boolean tdnaEnable = false;
        try {
            tdnaEnable = Boolean.parseBoolean(CommonUtils.getConnectorConfig("adaptive_authentication.tdna.enable", tenantDomain));
        } catch (IdentityEventException e) {
            log.error("Error while retrieve tenant configuration.", e);
        }

        TypingdnaEnableResponseDTO response = new TypingdnaEnableResponseDTO();
        response.setEnabled(tdnaEnable);
        return response;
    }

    /**
     * This function builds the URL that is used to make API calls.
     *
     * @param region TypingDNA API Region
     * @param userID Unique identifier of a user in TypingDNA.
     * @return URL that is used to call typingDNA API.
     */
    private String buildURL(String region, String userID) {

        return "https://api-" + region + ".typingdna.com/user" + "/" + userID;
    }

    /**
     * This function generates a unique identifier for users in TypingDNA.
     *
     * @param username     Name of the user.
     * @param tenantDomain Name of the tenant domain.
     * @return Hashed value tenant qualified username.
     */
    private String getUserID(String username, String tenantDomain) {

        return DigestUtils.sha256Hex(username + "@" + tenantDomain);
    }
}
