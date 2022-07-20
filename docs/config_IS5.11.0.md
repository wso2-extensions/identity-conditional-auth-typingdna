# Additional configurations to integrate TypingDNA with WSO2 Identity Server 5.11.0

1. (If pattern deletion required) Add the following configs to the deployment.toml to enable access to the typingdna functions api.
    ```
    [[resource.access_control]]
    context = "(.)/api/identity/typingdna/v1.0/(.)"
    secure = "true"
    http_method = "DELETE"
    permissions=["none"]
    scopes=["internal_login"]
    
    [tenant_context.rewrite]
    custom_webapps=["/api/identity/typingdna/v1.0/"]
    ```
2. Add the following line to basicauth.jsp in authenticationendpoint webapp after the list of page imports
    ```
    <jsp:directive.include file="plugins/typing-dna.jsp"/>
    ```
3. Copy https://github.com/wso2/identity-apps/blob/master/apps/authentication-portal/src/main/webapp/plugins/typing-dna.jsp to `authenticationendpoint/plugins` directory.


5. In the `typing-dna.jsp` file change the element id `username` to `usernameUserInput` in lines 32 and 37.


6. (Optional) Copy https://github.com/wso2/identity-apps/blob/master/apps/authentication-portal/src/main/webapp/js/typingdna.js to `authenticationendpoint/js` directory. This can help reduce load time of the auth endpoint.
