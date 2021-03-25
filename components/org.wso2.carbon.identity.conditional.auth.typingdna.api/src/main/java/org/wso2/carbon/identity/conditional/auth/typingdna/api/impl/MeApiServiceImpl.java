
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
