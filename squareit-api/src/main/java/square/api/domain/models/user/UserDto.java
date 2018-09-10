package square.api.domain.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import square.api.domain.constants.RoleTypes;
import square.api.domain.util.JsonUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto {

  private final String userName;

  private final String firstName;

  private final String lastName;

  private final String email;

  private final RoleTypes role;

  private final boolean enabled;

  private final String token;

  private UserDto(Builder builder) {
    userName = builder.userName;
    firstName = builder.firstName;
    lastName = builder.lastName;
    email = builder.email;
    role = builder.role;
    enabled = builder.enabled;
    token = builder.token;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getUserName() {
    return userName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public RoleTypes getRole() {
    return role;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public String getToken() {
    return token;
  }

  @Override
  public String toString() {
    return JsonUtil.writeObjectAsString(this);
  }

  public static final class Builder {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private RoleTypes role;
    private boolean enabled;
    private String token;

    private Builder() {
    }

    public Builder withUserName(String val) {
      userName = val;
      return this;
    }

    public Builder withFirstName(String val) {
      firstName = val;
      return this;
    }

    public Builder withLastName(String val) {
      lastName = val;
      return this;
    }

    public Builder withEmail(String val) {
      email = val;
      return this;
    }

    public Builder withRole(RoleTypes val) {
      role = val;
      return this;
    }

    public Builder withEnabled(boolean val) {
      enabled = val;
      return this;
    }

    public Builder withToken(String val) {
      token = val;
      return this;
    }

    public UserDto build() {
      return new UserDto(this);
    }
  }
}
