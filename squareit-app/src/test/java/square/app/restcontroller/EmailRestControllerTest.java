package square.app.restcontroller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.springframework.http.HttpStatus;

import square.api.domain.constants.Constraint;
import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.models.GenericMessageResponse;
import square.api.domain.models.user.CreateUserRequest;
import square.api.domain.models.user.UserDto;

import square.app.BaseSpringBootTest;
import square.app.constants.TimeConstants;
import square.app.domain.jpa.Users;
import square.app.utils.TestCheckers;
import square.app.utils.TestUsers;

public class EmailRestControllerTest extends BaseSpringBootTest {

  private static final String USER_URL = "/rest/user";

  private static final String EMAIL_URL = "/rest/email";

  private static CreateUserRequest.Builder requestBuilder;

  /**
   * Before each test.
   */
  @Before
  public void setUp() {
    tearDown();
    Assertions.assertThat(tokenRepository.findAll()).hasSize(0);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);

    requestBuilder = TestUsers.createCorrectUserRequestBuilder();
  }

  @After
  public void tearDown() {
    tokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  // Testing the email service too often will make Gmail block the system for some time. Usually one day. Use with care.
  @Ignore
  @Test
  public void registrationEmail_ShouldWork_CreateOneUserThenSendRegistrationMail() {
    //requestBuilder.withEmail("valid@email.com"); // Enter your own email to test it
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    final Users user = userRepository.findByEmail(response.getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final GenericMessageResponse regResp = callRegistrationGetOk(response.getToken());
    Assertions.assertThat(regResp.getMessage()).isEqualTo("Successfully mailed user");
    Assertions.assertThat(regResp.getToken()).isEqualTo(response.getToken());
  }

  @Ignore
  @Test
  public void resendRegistrationEmail_ShouldWork_CreateOneUserThenSendRegistrationMailThenResendRegistrationMail() {
    //requestBuilder.withEmail("valid@email.com"); // Enter your own email to test it
    final UserDto response = callCreateUserPutOk(requestBuilder);

    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    final Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final String regResMessage = callRegistrationGetOk(response.getToken()).getMessage();
    Assertions.assertThat(regResMessage).isEqualTo("Successfully mailed user");

    final GenericMessageResponse regResp = callResendRegistrationGetOk(requestBuilder.build().getEmail());
    Assertions.assertThat(regResp.getMessage()).isEqualTo("Successfully re-mailed user");
    Assertions.assertThat(regResp.getToken()).isNotEqualTo(response.getToken());
  }

  @Ignore
  @Test
  public void confirmRegistration_ShouldWork_CreateUserThenActivateUser() {
    //requestBuilder.withEmail("valid@email.com"); // Enter your own email to test it
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);
    Assertions.assertThat(user.getEnabled()).isEqualTo(false);

    final String regMessageResp = callConfirmRegistrationGetOk(response.getToken()).getMessage();
    Assertions.assertThat(regMessageResp).isEqualTo("Successfully activated user account");
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    user = userRepository.findByEmail(requestBuilder.build().getEmail());
    Assertions.assertThat(user.getEnabled()).isEqualTo(true);
  }

  @Ignore
  @Test
  public void confirmRegistration_ShouldFail_UserWithExpiredTokenShouldGetAMessageAboutIt() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(),
        ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).minusMonths(2);

    user.getVerificationToken().setExpiryDate(zonedDateTime);
    userRepository.save(user);

    final GenericMessageResponse regResp = callConfirmRegistrationGetOk(response.getToken());
    Assertions.assertThat(regResp.getMessage()).isEqualTo("Unsuccessful. User account not activated. New mail for "
        + "verification sent");
    Assertions.assertThat(regResp.getToken()).isEqualTo(response.getToken());
  }

  @Test
  public void registrationEmail_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";

    final ErrorInfo responseErr = callRegistrationGetError(HttpStatus.BAD_REQUEST, token);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "registrationEmail :: User not found with token: " + token);
  }

  @Test
  public void registrationEmail_ShouldFail_TokenNull() {
    final ErrorInfo responseErr = callRegistrationGetError(HttpStatus.BAD_REQUEST, null);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "registrationEmail :: Token is null or empty with token: null");
  }

  @Test
  public void registrationEmail_ShouldFail_TokenTooLong() {
    final String tokenTooLong = "tokentokentokentokentokentokentoken37";

    final ErrorInfo responseErr = callRegistrationGetError(HttpStatus.BAD_REQUEST, tokenTooLong);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "registrationEmail :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooLong);
  }

  @Test
  public void registrationEmail_ShouldFail_TokenTooShort() {
    final String tokenTooShort = "token";

    final ErrorInfo responseErr = callRegistrationGetError(HttpStatus.BAD_REQUEST, tokenTooShort);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "registrationEmail :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooShort);
  }

  @Test
  public void resendRegistrationEmail_ShouldFail_CorrectlyMadeEmailButItDoesNotHaveAUser() {
    final String emailWithNoUser = "TaleOfTimmy@email.com";

    final ErrorInfo responseErr = callResendRegistrationGetError(HttpStatus.BAD_REQUEST, emailWithNoUser);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "UserServiceImpl :: getUserByEmail :: User not found with email: " + emailWithNoUser);
  }

  @Test
  public void resendRegistrationEmail_ShouldFail_EmailNull() {
    final ErrorInfo responseErr = callResendRegistrationGetError(HttpStatus.BAD_REQUEST, null);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_LENGTH_MISMATCH,
        "resendRegistrationEmail :: Email is null or empty with email: null");
  }

  @Test
  public void resendRegistrationEmail_ShouldFail_EmailStringSaysNull() {
    final ErrorInfo responseErr = callResendRegistrationGetError(HttpStatus.BAD_REQUEST, "null");
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_LENGTH_MISMATCH,
        "resendRegistrationEmail :: Email is null or empty with email: null");
  }

  @Test
  public void resendRegistrationEmail_ShouldFail_EmailTooLong() {
    final String emailTooLong = "mailmailmailmailmailmailmailmailmailmailmailmailmailmailmailmail@mail.com";

    final ErrorInfo responseErr = callResendRegistrationGetError(HttpStatus.BAD_REQUEST, emailTooLong);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_LENGTH_MISMATCH,
        "resendRegistrationEmail :: Email must at most be " + Constraint.MAX_EMAIL
            + " characters with email: " + emailTooLong);
  }

  @Test
  public void resendRegistrationEmail_ShouldFail_EmailTooShort() {
    final String emailTooShort = "m@m.c";

    final ErrorInfo responseErr = callResendRegistrationGetError(HttpStatus.BAD_REQUEST, emailTooShort);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_LENGTH_MISMATCH,
        "resendRegistrationEmail :: Email must at least be " + Constraint.MIN_EMAIL
            + " characters with email: " + emailTooShort);
  }

  @Test
  public void resendRegistration_ShouldFail_ResendOfUserWhichIsAlreadyEnabled() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final String email = user.getEmail();
    user.setEnabled(true);
    userRepository.save(user);

    final ErrorInfo responseErr = callResendRegistrationGetError(HttpStatus.BAD_REQUEST, email);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_ALREADY_ACTIVATED,
        "resendRegistrationEmail :: User already registered with email: " + email);
  }

  @Test
  public void confirmRegistration_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";

    final ErrorInfo responseErr = callConfirmRegistrationGetError(HttpStatus.BAD_REQUEST, token);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "confirmRegistration :: User not found with token: " + token);
  }

  @Test
  public void confirmRegistration_ShouldFail_TokenNull() {
    final ErrorInfo responseErr = callConfirmRegistrationGetError(HttpStatus.BAD_REQUEST, null);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "confirmRegistration :: Token is null or empty with token: null");
  }

  @Test
  public void confirmRegistration_ShouldFail_TokenTooLong() {
    final String tokenTooLong = "tokentokentokentokentokentokentoken37";

    final ErrorInfo responseErr = callConfirmRegistrationGetError(HttpStatus.BAD_REQUEST, tokenTooLong);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "confirmRegistration :: Token must be " + Constraint.MAX_MIN_TOKEN
            + " characters with token: " + tokenTooLong);
  }

  @Test
  public void confirmRegistration_ShouldFail_TokenTooShort() {
    final String tokenTooShort = "token";

    final ErrorInfo responseErr = callConfirmRegistrationGetError(HttpStatus.BAD_REQUEST, tokenTooShort);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "confirmRegistration :: Token must be " + Constraint.MAX_MIN_TOKEN
            + " characters with token: " + tokenTooShort);
  }

  private UserDto callCreateUserPutOk(final CreateUserRequest.Builder request) {
    return httpPut(USER_URL + "/v1/upsertUser", HttpStatus.OK, request.build(), UserDto.class);
  }

  private GenericMessageResponse callRegistrationGetOk(final String token) {
    return httpGet(EMAIL_URL + "/v1/registrationEmail/" + token, HttpStatus.OK, GenericMessageResponse.class);
  }

  private ErrorInfo callRegistrationGetError(final HttpStatus expectedStatus, final String token) {
    return httpGetError(EMAIL_URL + "/v1/registrationEmail/" + token, expectedStatus, ErrorInfo.class);
  }

  private GenericMessageResponse callResendRegistrationGetOk(final String email) {
    return httpGet(EMAIL_URL + "/v1/resendRegistrationEmail/" + email, HttpStatus.OK, GenericMessageResponse.class);
  }

  private ErrorInfo callResendRegistrationGetError(final HttpStatus expectedStatus, final String email) {
    return httpGetError(EMAIL_URL + "/v1/resendRegistrationEmail/" + email, expectedStatus, ErrorInfo.class);
  }

  private GenericMessageResponse callConfirmRegistrationGetOk(final String token) {
    return httpGet(EMAIL_URL + "/v1/confirmRegistration/" + token, HttpStatus.OK, GenericMessageResponse.class);
  }

  private ErrorInfo callConfirmRegistrationGetError(final HttpStatus expectedStatus, final String token) {
    return httpGetError(EMAIL_URL + "/v1/confirmRegistration/" + token, expectedStatus, ErrorInfo.class);
  }
}
