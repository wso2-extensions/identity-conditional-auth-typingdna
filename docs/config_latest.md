# Configuring TypingDNA with WSO2 Identity Server

To use TypingDNA with WSO2 Identity Server 7.1 onwards, first you need to configure the authenticator with WSO2 Identity Server. If you are using a IS version older than IS 7.1.0 please follow this [document](config.md).

The following topics provide instructions on how to configure the TypingDNA with WSO2 Identity Server as a risk-based authentication (RBA) option:
* [Deploying TypingDNA Artifacts](#deploying-typingdna-artifacts)
* [Enabling TypingDNA in the WSO2 Identity Server](#enabling-typingdna-in-the-wso2-identity-server)
* [Setting up the TypingDNA account](#setting-up-the-typingdna-account)
* [Configuring the TypingDNA in WSO2 Identity Server](#configuring-the-typingdna-in-wso2-identity-server)
* [Configuring the application to use TypingDNA](#configuring-the-application-to-use-typingdna)

### Deploying TypingDNA Artifacts

You can either download the TypingDNA artifacts or build the authenticator from the source code.

1. To download the Github artifacts:  
   i. Stop WSO2 Identity Server if it is already running.   
   ii. Download the TypingDNA connector and other required artifacts from the [WSO2 store](https://store.wso2.com/connector/identity-conditional-auth-typingdna).  
   iii. Copy the `org.wso2.carbon.identity.conditional.auth.typingdna.functions-x.x.x.jar` file into the `<IS-Home>/repository/components/dropins` directory.   
   iv. Copy the `api#identity#typingdna#v_.war` file into the `<IS-Home>/repository/deployment/server/webapps` directory.

2. To build from the source code:  
   i. Stop WSO2 Identity Server if it is already running.  
   ii. To build the authenticator, navigate to the identity-conditional-auth-typingdna directory and execute the following command in a command prompt:

   ```
   mvn clean install
   ```

    * Note that the `org.wso2.carbon.identity.conditional.auth.typingdna.functions-x.x.x.jar` file is created in the `components/org.wso2.carbon.identity.conditional.auth.typingdna.functions/target` directory.
    * `api#identity#typingdna#v_.war` file is created in the `components/org.wso2.carbon.identity.conditional.auth.typingdna.api/target`
      directory.

    * Copy the org.wso2.carbon.identity.conditional.auth.typingdna.functions-x.x.x.jar file into the
      <IS-Home>/repository/components/dropins directory and the api#identity#typingdna#v_.war file into the <IS-Home>/repository/deployment/server/webapps directory.
      or
### Enabling TypingDNA in the WSO2 Identity Server

1. Stop WSO2 Identity Server if it is already running.
2. Add the below configuration in the `<IS-Home>/repository/conf/deployment.toml` file.

    ```
   [myaccount.security]
    enabled_features=["security.loginVerifyData.typingDNA"]
   ```

### Setting up the TypingDNA account

Follow the topics given below to set up your TypingDNA account.

#### Create a TypingDNA account

[Create your TypingDNA account](https://www.typingdna.com/clients/signup).
See [the instructions](files/Account%20Creation.pdf) for details.

#### Configuring TypingDNA API settings
Skip this part if you are using a developer/free TypingDNA account.

1. Login to typingdna with your account and configure the following.
2. Enable the **Auto-Enroll**, **Enable Force Initial Enrollments** and Update Settings.

   ![Alt text](images/API-Settings.png?raw=true)

### Configuring the TypingDNA in WSO2 Identity Server

1. Start the WSO2 Identity Server and log in to the console using admin credentials.
2. Go to `Login & Registration -> Other Settings -> TypingDNA Configuration`.
4. Enable TypingDNA and configure **API Key** and **Secret**. You can get the key and secret from the TypingDNA [dashboard](https://www.typingdna.com/clients/).
   [Learn more](files/Sign%20In.pdf).
5. Enable Advance TypingDNA-API mode if you have pro/enterprise typingDNA account (This advance mode will allow you
   to use TypingDNA’s advance APIs and configurations for the authentication).
6. Configure the region ( type **eu** or **us** ).

   ![Alt text](images/TypingDNA-Settings.png?raw=true)

### Configuring the application to use TypingDNA

1. Go to `Applications`, select the sample application you have configured, and click `Edit`.
2. Go to `Login Flow` tab.
3. Configure two authentication steps (2FA) in the login flow and use the Typing DNA adaptive script as shown below.

    ```
    // This script will step up 2FA authentication if the user's typing behaviour does not match with the enrolled behaviour.
    
    // You can use the parameters 'score' (num 0-100), 'result' (boolean), 'confidence' (num 0-100), 'comparedPatterns' in your 
    // authentication logic to trigger the 2nd step. 
    // Only the 'result' parameter has been used in the sample script. 
    
    var onLoginRequest = function(context) {
        executeStep(1, {
            onSuccess: function (context) {
                verifyUserWithTypingDNA(context, {
                    onSuccess: function(context,data){
                        // Change the definition here as required.
                        var userVerified = data.result;
    
                        // data.isTypingPatternReceived indicates whether a typing pattern is received from the login portal.
                        if (data.isTypingPatternReceived && !userVerified){
                            executeStep(2);
                        }
                    },onFail: function(context,data){
                        executeStep(2);
                    }
                });
            }
        });
    };
   ```

   ![Alt_text](images/LoginFlow.png?raw=true)   
   ![Alt_text](images/AdaptiveScript.png?raw=true)

   Learn more about [TypingDNA adaptive functions](files/adaptive-script-description.md).
