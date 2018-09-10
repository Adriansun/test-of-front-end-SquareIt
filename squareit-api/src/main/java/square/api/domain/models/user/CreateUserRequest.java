package square.api.domain.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import square.api.domain.constants.Constraint;
import square.api.domain.constants.RoleTypes;
import square.api.domain.util.JsonUtil;
import square.api.domain.validators.ValidateEmail;
import square.api.domain.validators.ValidatePasswords;
import square.api.domain.validators.ValidateRoleType;
import square.api.domain.validators.ValidateUsername;

@ValidatePasswords(message = "Passwords validation error")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = CreateUserRequest.Builder.class)
public class CreateUserRequest {

  @ValidateUsername(message = "Username validation error")
  private final String userName;

  @NotBlank(message = "First name may not be null, empty, or contain one whitespace")
  @Size(max = Constraint.MAX_FIRSTNAME, message = "First name may not be more than " + Constraint.MAX_FIRSTNAME
      + " characters")
  private final String firstName;

  @NotNull(message = "Last name may not be null")
  @Size(max = Constraint.MAX_LASTNAME, message = "Last name may not be more than " + Constraint.MAX_LASTNAME
      + " characters")
  private final String lastName;

  @ValidateEmail(message = "Email validation error")
  private final String email;

  private final String password;

  private final String confirmPassword;

  @ValidateRoleType(message = "RoleType validation error")
  private final RoleTypes role;

  private final boolean enabled;

  private CreateUserRequest(Builder builder) {
    userName = builder.userName;
    firstName = builder.firstName;
    lastName = builder.lastName;
    email = builder.email;
    password = builder.password;
    confirmPassword = builder.confirmPassword;
    role = builder.role;
    enabled = builder.enabled;
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

  public String getPassword() {
    return password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public RoleTypes getRole() {
    return role;
  }

  public boolean getEnabled() {
    return enabled;
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
    private String password;
    private String confirmPassword;
    private RoleTypes role;
    private boolean enabled = false;

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

    public Builder withPassword(String val) {
      password = val;
      return this;
    }

    public Builder withConfirmPassword(String val) {
      confirmPassword = val;
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

    public CreateUserRequest build() {
      return new CreateUserRequest(this);
    }
  }
}
