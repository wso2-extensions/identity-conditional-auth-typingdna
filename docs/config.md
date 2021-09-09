# identity-conditional-auth-typingdna

### Installing the connector & extensions  

#### Adding plugins to authentication endpoint.
1. Download the resources/typing-dna folder

2. Copy the files inside plugins folder and paste them to IS_Home/repository/deployment/server/webapps/authenticationendpoint/plugins folder.

3. Add the typing-dna.js file to IS_Home/repository/deployment/server/webapps/authenticationendpoint/js folder.

#### Deploying TypingDNA Artifacts.
You can either download the TypingDNA artifacts or build the authenticator from the source code.
1. To download the Github artifacts:  
i. Stop WSO2 Identity Server if it is already running.   
ii. Download the [TypingDNA connector]() & add it to `IS-Home/repository/components/dropins` folder.    
iii. Download the [TypingDNA API]() and add it to `IS-Home/repository/deployment/server/webapps` folder.  
  

2. To build from the source code.  
i. Stop WSO2 Identity Server if it is already running.  
ii. To build the authenticator, navigate to the identity-conditional-auth-typingdna directory and execute the following 
   command in a command prompt.  
   ```mvn clean install```

   Note that the `org.wso2.carbon.identity.conditional.auth.typingdna.functions-x.x.x.jar` file is created in 
   the `components/org.wso2.carbon.identity.conditional.auth.typingdna.functions/target` directory and 
   `api#identity#typingdna#v_.war` file is created in the `components/org.wso2.carbon.identity.conditional.auth.typingdna.api/target` 
   directory.  
   Copy the `org.wso2.carbon.identity.conditional.auth.typingdna.functions-x.x.x.jar` file into the 
   `<IS-Home>/repository/components/dropins directory` and `api#identity#typingdna#v_.war` file into the
   `<IS-Home>/repository/deployment/server/webapps`

### Setting up a TypingDNA account  

#### Create a TypingDNA account.
You can craete a TypingDNA account from [here](https://www.typingdna.com/clients/signup).
Refer [this doc](files/Account%20Creation.pdf) for detailed information.


#### Configuring TypingDNA API settings.
Skip this part if you are using developer/free TypingDNA account.

1. Login to typingdna with your account and Configure the following.
2. Enable the Auto-Enroll & Enable Force Initial Enrollments & Update Settings.

![Alt text](images/screen-shot-2.png?raw=true)

### Configuring the Identity provider

1. Login to console
2. Go to Manage -> configurations -> other settings
3. Select/ Scroll down to TypingDNA Configuration
4. Enable TypingDNA & configure API Key, Secret. You can get the Key & Secret from TypingDNA
   [dashboard](https://www.typingdna.com/clients/).
   Refer [this doc](files/Sign%20In.pdf) for detailed information.
5. Enable Advance TypingDNA-API mode if you have pro/enterprise typingDNA account - This advance mode will allow you
   to use TypingDNA’s advance APIs & configurations for the authentication.  
6. Configure the region ( type eu or us )

![Alt text](images/screen-shot-3.png?raw=true)

### Configuring Service provider

1. Go to Develop -> Application & Select the sample application you configured
2. Go to sign-on method
3. Add Typing-Biometric-Based in templates->user  
   Refer [this doc](files/adaptive-script-description.md) to get detailed information about TypingDNA adaptive template.

![Alt_text](images/screen-shot-4.png?raw=true)
