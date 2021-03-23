package org.wso2.carbon.identity.conditional.auth.typingdna.api.impl;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.ApiResponseMessage;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.MeApiService;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.core.TypingDNAServiceImpl;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.ws.rs.core.Response;

/**
 * Implements the API that is used to delete users' typing patterns in TypingDNA.
 */
public class MeApiServiceImpl extends MeApiService {

    /**
     * Function that is used to delete users' typing patterns in TypingDNA.
     *
     * @return ok response if delete successful, error response if delete failed with a message.
     */
    @Override
    public Response meTypingpatternsDelete() {

        try {
            TypingDNAServiceImpl deletePatterns = new TypingDNAServiceImpl();
            deletePatterns.deleteTypingPatterns();
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Typing Patterns deleted")).build();
        } catch (UnknownHostException e) {
            return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "Error. Can not connect to TypingDNA")).build();
        } catch (IOException e) {
            return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "Error in server configuration")).build();
        }
    }

}
