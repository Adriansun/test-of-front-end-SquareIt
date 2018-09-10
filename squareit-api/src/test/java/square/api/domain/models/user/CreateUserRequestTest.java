package square.api.domain.models.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.io.IOException;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import square.api.domain.constants.Constraint;
import square.api.domain.util.JsonUtil;
import square.api.domain.utils.TestObjects;

@ActiveProfiles({"dev"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CreateUserRequestTest {

  private static final String TOO_LONG_STRING = "ThisStringIsTooLongToBeAccepted";

  private static CreateUserRequest.Builder request;

  private static Validator validator;

  private static String errorMsg;

  /**
   * Setup.
   */
  @Before
  public void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    request = TestObjects.createCorrectUserRequestBuilder();
  }

  @Test
  public void createUserRequest_ShouldWork_CreateCompleteObjectWithCorrectValues() {
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());
    Assert.assertTrue(violations.isEmpty());
  }

  @Test
  public void createUserRequest_ShouldWork_MarshallObject() {
    final String serializedString = JsonUtil.writeObjectAsString(request.build());
    Assertions.assertThat(serializedString).isEqualTo(request.build().toString());
  }

  @Test
  public void createUserRequest_ShouldWork_UnMarshallObject() throws IOException {
    final CreateUserRequest createUserRequest = request.build();

    final CreateUserRequest readValue = JsonUtil.getJsonMapper()
        .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
        .readValue(createUserRequest.toString(), CreateUserRequest.class);

    Assertions.assertThat(readValue.getUserName()).isEqualTo(createUserRequest.getUserName());
    Assertions.assertThat(readValue.getFirstName()).isEqualTo(createUserRequest.getFirstName());
    Assertions.assertThat(readValue.getLastName()).isEqualTo(createUserRequest.getLastName());
    Assertions.assertThat(readValue.getEmail()).isEqualTo(createUserRequest.getEmail());
    Assertions.assertThat(readValue.getPassword()).isEqualTo(createUserRequest.getPassword());
    Assertions.assertThat(readValue.getConfirmPassword()).isEqualTo(createUserRequest.getConfirmPassword());
    Assertions.assertThat(readValue.getEnabled()).isEqualTo(createUserRequest.getEnabled());
    Assertions.assertThat(readValue.getRole()).isEqualTo(createUserRequest.getRole());
  }

  @Test
  public void createUserRequest_ShouldFail_UsernameNull() {
    request.withUserName(null);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Username may not be null or empty");
  }

  @Test
  public void createUserRequest_ShouldFail_UsernameEmpty() {
    request.withUserName("");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Username may not be null or empty");
  }

  @Test
  public void createUserRequest_ShouldFail_UsernameTooLong() {
    request.withUserName(TOO_LONG_STRING);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Username may not be more than " + Constraint.MAX_USERNAME
        + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_UsernameTooShort() {
    request.withUserName("A");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Username may not be less than " + Constraint.MIN_USERNAME
        + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_UsernameNotAllowedSpecialCharacter() {
    request.withUserName("Batman_ 1@");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Username may only use lowercase lowercase / uppercase letters, "
        + "numbers 0-9, underscores and whitespaces");
  }

  @Test
  public void createUserRequest_ShouldFail_FirstNameNull() {
    request.withFirstName(null);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("First name may not be null, empty, or contain one whitespace");
  }

  @Test
  public void createUserRequest_ShouldFail_FirstNameEmpty() {
    request.withFirstName("");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("First name may not be null, empty, or contain one whitespace");
  }

  @Test
  public void createUserRequest_ShouldFail_FirstNameWithBlankSpace() {
    request.withFirstName(" ");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("First name may not be null, empty, or contain one whitespace");
  }

  @Test
  public void createUserRequest_ShouldFail_FirstNameTooLong() {
    request.withFirstName(TOO_LONG_STRING);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("First name may not be more than " + Constraint.MAX_FIRSTNAME
        + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_LastNameNull() {
    request.withLastName(null);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Last name may not be null");
  }

  @Test
  public void createUserRequest_ShouldWork_LastNameEmpty() {
    request.withLastName("");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertTrue(violations.isEmpty());
  }

  @Test
  public void createUserRequest_ShouldFail_LastNameTooLong() {
    request.withLastName(TOO_LONG_STRING);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Last name may not be more than " + Constraint.MAX_LASTNAME
        + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_EmailNull() {
    request.withEmail(null);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Email may not be null or empty");
  }

  @Test
  public void createUserRequest_ShouldFail_EmailEmpty() {
    request.withEmail("");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Email may not be null or empty");
  }

  @Test
  public void createUserRequest_ShouldFail_EmailTooLong() {
    request.withEmail(TOO_LONG_STRING + "letsAddMoreCharactersBecauseEmailAcceptMoreCharacters");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Email may not be more than " + Constraint.MAX_EMAIL + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_EmailTooShort() {
    request.withEmail("2");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Email may not be less than " + Constraint.MIN_EMAIL + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_EmailMatcherMismatch() {
    request.withEmail("hello@@mayo.org");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Email does not follow pattern restrictions");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordNull() {
    request.withPassword(null);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password may not be null or empty");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordEmpty() {
    request.withPassword("");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password may not be null or empty");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordTooLong() {
    request.withPassword(TOO_LONG_STRING);
    request.withConfirmPassword(TOO_LONG_STRING);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password may not be more than " + Constraint.MAX_PASSWORD
        + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordTooShort() {
    request.withPassword("hey");
    request.withConfirmPassword("hey");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password must be at least " + Constraint.MIN_PASSWORD + " characters");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordAndConfirmPasswordMismatch() {
    request.withConfirmPassword("IamABadPassMatch");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password and confirm password does not match");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordDoesNotHaveASpecialCharacter() {
    request.withPassword("IAmWithOutSpecialCharacter");
    request.withConfirmPassword("IAmWithOutSpecialCharacter");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password must have at least "
        + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " special character");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordDoesNotHaveANumber() {
    request.withPassword("abcdeF!two");
    request.withConfirmPassword("abcdeF!two");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password must have at least "
        + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " number");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordDoesNotAHaveUppercaseCharacter() {
    request.withPassword("abcdef!2");
    request.withConfirmPassword("abcdef!2");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password must have at least "
        + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " UPPERCASE character");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordDoesNotAHaveLowercaseCharacter() {
    request.withPassword("ABCDEF!2");
    request.withConfirmPassword("ABCDEF!2");
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Password must have at least "
        + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " lowercase character");
  }

  @Test
  public void createUserRequest_ShouldFail_RoleNull() {
    request.withRole(null);
    final Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Role type may not be null");
  }

  private String getErrorMessage(final Set<ConstraintViolation<CreateUserRequest>> violations) {
    final String[] errorMsg = {null};
    violations.iterator().forEachRemaining(msg -> errorMsg[0] = msg.getMessage());

    return errorMsg[0];
  }
}
