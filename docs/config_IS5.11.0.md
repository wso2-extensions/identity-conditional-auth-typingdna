# Additional configurations to integrate TypingDNA with WSO2 Identity Server 5.11.0


1. Add the following line to basicauth.jsp in authenticationendpoint webapp after the list of page imports
    ```
    <jsp:directive.include file="plugins/typing-dna.jsp"/>
    ```
2. Copy https://github.com/wso2/identity-apps/blob/master/apps/authentication-portal/src/main/webapp/plugins/typing-dna.jsp to `authenticationendpoint/plugins` directory.


3. In the `typing-dna.jsp` file change the element id `username` to `usernameUserInput` in lines 32 and 37.


4. (Optional) Copy https://github.com/wso2/identity-apps/blob/master/apps/authentication-portal/src/main/webapp/js/typingdna.js to `authenticationendpoint/js` directory. This can help reduce load time of the auth endpoint.
