## identity-conditional-auth-typingdna

*skip the first two steps if you have Identity Server with configured sample Application.

 1. Before starting with Typing Biometric Authentication. You should set up an identity server.


 2. Then deploy a sample application
  

 3. After that you need to add the connector & extensions to the folder
   ```
    1. Download the resources/typing-dna folder
    
    2. Copy the files inside plugins folder and paste them to IS_Home/repository/deployment/server/webapps/authenticationendpoint/plugins
    *copy the whole plugins folder if there is no plugins inside the authentication endpoint already 
    
    3. Add the typing-dna.js file to IS_Home/repository/deployment/server/webapps/authenticationendpoint/js
   ```
 ![Alt text](resources/images/screen-shot-1.png?raw=true)
   ```
    4. Add the following line in IS_Home/repository/deployment/server/webapps/authenticationendpoint/basicauth.jsp
          <jsp:directive.include file="plugins/basicauth-extensions.jsp"/>
   
    5. Add the typingdna.json to IS_Home/repository/resources/identity/authntemplates
    
    6. Build the project & add identity-conditional-auth-typingdna__.jar to IS_Home/repository/components/dropins folder
   ```
 

 4. Configuring TypingDNA [console](https://www.typingdna.com/clients/apisettings)
   ```
    Login to typingdna with your account and Configure the following 
    ( You can quickly create a free account if you don't have)

    Enable the Auto-Enroll & Enable Force Initial Enrollments & Update Settings
   ```
 ![Alt text](resources/images/screen-shot-2.png?raw=true)

 5. Configuring the Identity provider
  ```
  Login to console
  
  Go to Manage -> configurations -> other settings
  
  Select/ Scroll down to TypingDNA Configuration
  
  Enable TypingDNA & configure API , Secret
  
  Enable Advanced mode if you have pro/enterprise typingDNA account
  
  Configure the region ( type eu or us)
  ```
 ![Alt text](resources/images/screen-shot-3.png?raw=true)

6. Configuring Service provider
  ```
  Go to Develop -> Application & Select the sample application you configured
  
  Go to sign-on method
  
  Add Typing-Biometric-Based in templates->user
  ```
