package org.wso2.carbon.identity.conditional.auth.typingdna.api.factories;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.MeApiService;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.impl.MeApiServiceImpl;

public class MeApiServiceFactory {

   private final static MeApiService service = new MeApiServiceImpl();

   public static MeApiService getMeApi()
   {
      return service;
   }
}
