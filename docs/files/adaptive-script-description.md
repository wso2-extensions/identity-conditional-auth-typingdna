## TypingDNA adaptive script documentation.

### Adaptive script parameters description.
verifyUserWithTypingDNA function is used to verify the users typing behaviour with typingdna and returns the 
following parameters with data.

| No | Parameter | Description | Sample values |
| ------- | ----------- | ------------- | ------------- |
| 1 | data.result | A boolean, indicates whether a user's typing pattern is matched with previously enrolled typing patterns | True - Typing patterns matched<br>False - Typing patterns not matched |  
| 2 | data.score | A number, between 0 - 100 indicates how well the typing pattern matches with previously enrolled patterns | 0 - Poor matching<br>100 - Well matching | 
| 3 | data.confidence | A number, between 0-100 indicates how much confidence TypingDNA has in their result. Can be the measure of accuracy | 0 - Poor confidence<br>100 - Good confidence |
| 4 | data.comparedPatterns | A number, between 1-20 indicates how many previously enrolled patterns were used to verify current typing pattern | 1 - minimum patterns<br>20 - Maximum patterns |
| 5 | data.isTypingPatternReceived | A boolean, indicates whether the typing pattern was received from the user. Typing patterns may not be received from users if the admin didn’t deploy the extensions in the login page or if the end users are using the autofill option in the browser | True - Typing patterns has been received<br>False - Typing patterns hasn’t been received. |

Parameters 2, 3, 4 are only available for the [paid typingdna subscriptions](https://www.typingdna.com/pricing.html). 
Otherwise those values will be null.




### Using the parameters inside the script to verify the user.
In the script template provided data.result has been used to verify the user and prompt the second step. 
Instead of that, admin can use other parameters in following ways to verify the user. Please note that below things can 
be configured if you have [paid_typingdna_subscriptions](https://www.typingdna.com/pricing.html). Otherwise you can use
the template without changing it.  
 ![Alt text](../images/screen-shot-5.png?raw=true)

1. data.score  
If the score is less than a threshold, the user can be prompted to the second step.  
   Eg :- ```var userVerified = data.score > 60 ;```
2. data.confidence  
If the score is less than a threshold, the user can be prompted to the second step.  
   Eg :- ```var userVerified = data.confidence > 50 ;```

3. data.comparedPatterns    
Compared patterns can be used with other other parameters to increase the security.  
   Eg 1 :-  ```var userVerified = data.score > 60 && data.comparedPatterns > 4 ;```  
   Eg 2 :-  ```var userVerified = data.result && data.comparedPatterns > 5 ;```


