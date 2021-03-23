package org.wso2.carbon.identity.conditional.auth.typingdna.api.impl;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.ServerApiService;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.core.TypingDNAServiceImpl;

import javax.ws.rs.core.Response;

public class ServerApiServiceImpl extends ServerApiService {

    /**
     * Function that is used to retrieve whether typingdna enabled or not in tenant.
     *
     * @return enable true if typingdna enabled in tenant else false.
     */
    @Override
    public Response serverTypingdnaConfigGet() {

        TypingDNAServiceImpl enable = new TypingDNAServiceImpl();
        return Response.ok().entity(enable.getEnabled()).build();
    }
}