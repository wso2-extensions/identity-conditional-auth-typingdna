package org.wso2.carbon.identity.conditional.auth.typingdna.api;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.dto.*;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.factories.ServerApiServiceFactory;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.dto.TypingdnaEnableResponseDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/server")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(value = "/server", description = "the server API")
public class ServerApi  {

   private final ServerApiService delegate = ServerApiServiceFactory.getServerApi();

    @GET
    @Path("/typingdnaConfig")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve whether typingdna authentication enabled by admin", notes = "This API is used to retrieve whether the typingdna authentication enabled by admin or not.\n<b>Permission required:</b>\n    * none\n<b>Scope required:</b>\n    * internal_login\n", response = TypingdnaEnableResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Resource Forbidden"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error") })

    public Response serverTypingdnaConfigGet()
    {
    return delegate.serverTypingdnaConfigGet();
    }
}

