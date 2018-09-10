package square.api.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import square.api.domain.util.JsonUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericMessageResponse {

  private String message;

  private String token;

  /**
   * GenericMessageResponse constructor.
   *
   * @param message message
   * @param token   token
   * @return return
   */
  public GenericMessageResponse withMessage(String message, String token) {
    this.message = message;
    this.token = token;
    return this;
  }

  @Override
  public String toString() {
    return JsonUtil.writeObjectAsString(this);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
