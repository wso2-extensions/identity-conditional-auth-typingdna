package org.wso2.carbon.identity.conditional.auth.typingdna.api.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class TypingdnaEnableResponseDTO  {
  
  
  
  private Boolean enabled = null;

  
  /**
   * Whether typingdna authentication enabled or not
   **/
  @ApiModelProperty(value = "Whether typingdna authentication enabled or not")
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
    sb.append("class TypingdnaEnableResponseDTO {\n");
    
    sb.append("  enabled: ").append(enabled).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
