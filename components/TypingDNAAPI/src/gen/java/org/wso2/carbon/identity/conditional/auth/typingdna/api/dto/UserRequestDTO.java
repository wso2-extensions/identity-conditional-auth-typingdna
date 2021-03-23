package org.wso2.carbon.identity.conditional.auth.typingdna.api.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class UserRequestDTO  {
  
  
  public enum ActionEnum {
     DELETE, 
  };
  @NotNull
  private ActionEnum action = null;
  
  
  private Integer verificationCode = null;

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("action")
  public ActionEnum getAction() {
    return action;
  }
  public void setAction(ActionEnum action) {
    this.action = action;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("verificationCode")
  public Integer getVerificationCode() {
    return verificationCode;
  }
  public void setVerificationCode(Integer verificationCode) {
    this.verificationCode = verificationCode;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserRequestDTO {\n");
    
    sb.append("  action: ").append(action).append("\n");
    sb.append("  verificationCode: ").append(verificationCode).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
