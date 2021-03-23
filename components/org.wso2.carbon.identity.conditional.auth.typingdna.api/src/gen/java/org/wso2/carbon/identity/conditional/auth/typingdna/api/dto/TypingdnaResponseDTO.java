package org.wso2.carbon.identity.conditional.auth.typingdna.api.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class TypingdnaResponseDTO  {
  
  
  
  private Boolean success = null;
  
  
  private Boolean enabled = null;

  
  /**
   * Whether typingpatterns deleted successfully or not
   **/
  @ApiModelProperty(value = "Whether typingpatterns deleted successfully or not")
  @JsonProperty("success")
  public Boolean getSuccess() {
    return success;
  }
  public void setSuccess(Boolean success) {
    this.success = success;
  }

  
  /**
   * Whether typingpatterns
   **/
  @ApiModelProperty(value = "Whether typingpatterns")
  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TypingdnaResponseDTO {\n");
    
    sb.append("  success: ").append(success).append("\n");
    sb.append("  enabled: ").append(enabled).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
