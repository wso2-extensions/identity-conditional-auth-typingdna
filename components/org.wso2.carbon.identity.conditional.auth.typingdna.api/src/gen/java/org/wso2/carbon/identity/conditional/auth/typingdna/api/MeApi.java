package org.wso2.carbon.identity.conditional.auth.typingdna.api;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.dto.*;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.factories.MeApiServiceFactory;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.dto.TypingdnaResponseDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/me")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(value = "/me", description = "the me API")
public class MeApi  {

   private final MeApiService delegate = MeApiServiceFactory.getMeApi();

    @DELETE
    @Path("/typingpatterns")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Resets TOTP credentials of the authenticated user.", notes = "This API is used to delete the TOTP credentials of the authenticated user. <b>Permission required:</b><br> * none<br> <b>Scope required:</b><br> * internal_login\n", response = TypingdnaResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Credentials deleted successfully"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Resource Forbidden"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error") })

    public Response meTypingpatternsDelete()
    {
    return delegate.meTypingpatternsDelete();
    }
}

