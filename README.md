# identity-conditional-auth-typingdna

*skip the first two steps if you have Identity Server with configured sample Application.

1.Before starting with Typing Biometric Authentication. You should set up an identity server. 

2.Then deploy a sample application 
  
3. After that you need to add the connector & extensions to the folder


Adding TypingDNA plugins in authentication endpoint.
1. Download typing-dna.zip & extract it 
2. Copy the files inside plugins folder and paste them to IS_Home/repository/deployment/server/webapps/authenticationendpoint/plugins
*copy the whole plugins folder if there is no plugins inside the authentication endpoint already 
3. Add the typing-dna.js file to IS_Home/repository/deployment/server/webapps/authenticationendpoint/js

Adding typing dna connector to the identity server
Download the typingdnafunctions.jar and add it to IS_Home/repository/components/dropins folder


4. Configuring the Identity provider

Login to console
Go to Manage -> configurations -> other settings
Select/ Scroll down to TypingDNA Configuration
Enable TypingDNA & configure API , Secret
Enable Advanced mode if you have pro typingDNA account
Select region eu/us


5. Configuring TypingDNA console

Login to typingdna with your account and Configure the following ( use the following link) 
https://www.typingdna.com/clients/apisettings

Enable the Auto-Enroll & Enable Force Initial Enrollments & Update Settings





6. Configuring Service provider
Go to Develop -> Application & Select the sample application you configured
Go to sign-on method
Add Typing-Biometric-Based in templates->user

