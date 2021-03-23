package org.wso2.carbon.identity.conditional.auth.typingdna.api.factories;

import org.wso2.carbon.identity.conditional.auth.typingdna.api.ServerApiService;
import org.wso2.carbon.identity.conditional.auth.typingdna.api.impl.ServerApiServiceImpl;

public class ServerApiServiceFactory {

   private final static ServerApiService service = new ServerApiServiceImpl();

   public static ServerApiService getServerApi()
   {
      return service;
   }
}
