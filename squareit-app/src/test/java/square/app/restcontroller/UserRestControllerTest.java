package square.app.restcontroller;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import square.api.domain.constants.Constraint;
import square.api.domain.constants.RoleTypes;
import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.models.GenericMessageResponse;
import square.api.domain.models.user.CreateUserRequest;
import square.api.domain.models.user.UserDto;

import square.app.BaseSpringBootTest;
import square.app.constants.TimeConstants;
import square.app.domain.jpa.Users;
import square.app.domain.jpa.VerificationToken;
import square.app.utils.TestCheckers;
import square.app.utils.TestUsers;

public class UserRestControllerTest extends BaseSpringBootTest {

  private static final String USER_URL = "/rest/user";

  private static CreateUserRequest.Builder requestBuilder;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private TestCheckers testCheckers;

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

  @Test
  public void createUser_ShouldWork_CreateOneUser() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    final Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    testCheckers = new TestCheckers(passwordEncoder);
    testCheckers.checkHashedPassword(requestBuilder.build(), user);
  }

  @Test
  public void createUser_ShouldWork_CreateTwoUserWithDifferentEmailsAndDifferentUserNames() {
    final UserDto response1 = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("hello@there.com");
    request2.withUserName("Superman");

    final UserDto response2 = callCreateUserPutOk(request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);

    final Users user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    final Users user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response1, user1,
        false);
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(request2.build(), response2, user2,
        false);
  }

  @Test
  public void createUser_ShouldFail_CreateTwoUsersWithTheSameUsernameAndDifferentEmails() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("hello@there.com");

    callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.BAD_REQUEST, request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_ALREADY_EXISTS,
        "UserServiceImpl :: checkUserExistence :: User already exist with username: Batman_ 1");
  }

  @Test
  public void createUser_ShouldFail_CreateTwoUsersWithTheSameEmailAndDifferentUsernames() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withUserName("Knas");

    callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.BAD_REQUEST, request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_ALREADY_EXISTS,
        "UserServiceImpl :: checkUserExistence :: User already exist with email: hans@larsson.com");
  }

  @Test
  public void createUser_ShouldFail_CreateTwoUsersWithTheSameUsernamesAndDifferentEmail() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("somthing@go.com");

    callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.BAD_REQUEST, request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_ALREADY_EXISTS,
        "UserServiceImpl :: checkUserExistence :: User already exist with username: Batman_ 1");
  }

  @Test
  public void createUser_ShouldFail_UserNameEmpty() {
    requestBuilder.withUserName("");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USERNAME_NULL_OR_EMPTY,
        "Username may not be null or empty");
  }

  @Test
  public void createUser_ShouldFail_UserNameNull() {
    requestBuilder.withUserName(null);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USERNAME_NULL_OR_EMPTY,
        "Username may not be null or empty");
  }

  @Test
  public void createUser_ShouldFail_UserNameTooLong() {
    requestBuilder.withUserName("BatmanIsAManAndNotSpidermanLand");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USERNAME_LENGTH_MISMATCH,
        "Username may not be more than " + Constraint.MAX_USERNAME + " characters");
  }

  @Test
  public void createUser_ShouldFail_UserNameTooShort() {
    requestBuilder.withUserName("B");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USERNAME_LENGTH_MISMATCH,
        "Username may not be less than " + Constraint.MIN_USERNAME + " characters");
  }

  @Test
  public void createUser_ShouldFail_UsernameMalformed() {
    requestBuilder.withUserName("@Ff777");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USERNAME_VALIDATION_ERROR,
        "Username may only use lowercase lowercase / uppercase letters, numbers 0-9, underscores and "
            + "whitespaces");
  }

  @Test
  public void createUser_ShouldFail_FirstNameEmpty() {
    requestBuilder.withFirstName("");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_FIRST_NAME_NULL_OR_EMPTY_OR_BLANK_SPACE,
        "First name may not be null, empty, or contain one whitespace");
  }

  @Test
  public void createUser_ShouldFail_FirstNameWithOneBlankSpace() {
    requestBuilder.withFirstName(" ");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_FIRST_NAME_NULL_OR_EMPTY_OR_BLANK_SPACE,
        "First name may not be null, empty, or contain one whitespace");
  }

  @Test
  public void createUser_ShouldFail_FirstNameNull() {
    requestBuilder.withFirstName(null);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_FIRST_NAME_NULL_OR_EMPTY_OR_BLANK_SPACE,
        "First name may not be null, empty, or contain one whitespace");
  }

  @Test
  public void createUser_ShouldFail_FirstNameTooLong() {
    requestBuilder.withFirstName("Alessandronozorrodenerohippikulla");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_FIRST_NAME_LENGTH_MISMATCH,
        "First name may not be more than " + Constraint.MAX_FIRSTNAME + " characters");
  }

  @Test
  public void createUser_ShouldWork_LastNameEmpty() {
    requestBuilder.withLastName("");

    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);

    final Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);
  }

  @Test
  public void createUser_ShouldFail_LastNameNull() {
    requestBuilder.withLastName(null);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_LAST_NAME_NULL,
        "Last name may not be null");
  }

  @Test
  public void createUser_ShouldFail_LastNameTooLong() {
    requestBuilder.withLastName("Lastnameareuswholikelonglastnames");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_LAST_NAME_TOO_LONG,
        "Last name may not be more than " + Constraint.MAX_LASTNAME + " characters");
  }

  @Test
  public void createUser_ShouldFail_EmailEmpty() {
    requestBuilder.withEmail("");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_NULL_OR_EMPTY,
        "Email may not be null or empty");
  }

  @Test
  public void createUser_ShouldFail_EmailNull() {
    requestBuilder.withEmail(null);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_NULL_OR_EMPTY,
        "Email may not be null or empty");
  }

  @Test
  public void createUser_ShouldFail_EmailTooLong() {
    requestBuilder.withEmail("mailmailmailmailmailmailmailmailmailmailmailmailmailmailmailmail@mail.com");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_LENGTH_MISMATCH,
        "Email may not be more than " + Constraint.MAX_EMAIL + " characters");
  }

  @Test
  public void createUser_ShouldFail_EmailTooShort() {
    requestBuilder.withEmail("m@m.c");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_LENGTH_MISMATCH,
        "Email may not be less than " + Constraint.MIN_EMAIL + " characters");
  }

  @Test
  public void createUser_ShouldFail_EmailMalformed() {
    requestBuilder.withEmail("mail@mail");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.EMAIL_VALIDATION_ERROR,
        "Email does not follow pattern restrictions");
  }

  @Test
  public void createUser_ShouldFail_PasswordEmpty() {
    requestBuilder.withPassword("");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_NULL_OR_EMPTY,
        "Password may not be null or empty");
  }

  @Test
  public void createUser_ShouldFail_PasswordNull() {
    requestBuilder.withPassword(null);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_NULL_OR_EMPTY,
        "Password may not be null or empty");
  }

  @Test
  public void createUser_ShouldFail_PasswordTooLong() {
    requestBuilder.withPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ");
    requestBuilder.withConfirmPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_LENGTH_MISMATCH,
        "Password may not be more than " + Constraint.MAX_PASSWORD + " characters");
  }

  @Test
  public void createUser_ShouldFail_PasswordTooShort() {
    requestBuilder.withPassword("abc");
    requestBuilder.withConfirmPassword("abc");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_LENGTH_MISMATCH,
        "Password must be at least " + Constraint.MIN_PASSWORD + " characters");
  }

  @Test
  public void createUser_ShouldFail_PasswordDoesNotHaveASpecialCharacter() {
    requestBuilder.withPassword("IAmWithOutSpecialCharacter");
    requestBuilder.withConfirmPassword("IAmWithOutSpecialCharacter");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_VALIDATION_ERROR,
        "Password must have at least " + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER
            + " special character");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordDoesNotHaveANumber() {
    requestBuilder.withPassword("abcdeF!two");
    requestBuilder.withConfirmPassword("abcdeF!two");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_VALIDATION_ERROR,
        "Password must have at least " + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " number");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordDoesNotHaveAUppercaseCharacter() {
    requestBuilder.withPassword("abcdef!2");
    requestBuilder.withConfirmPassword("abcdef!2");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_VALIDATION_ERROR,
        "Password must have at least " + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER
            + " UPPERCASE character");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordDoesNotHaveALowercaseCharacter() {
    requestBuilder.withPassword("ABCDEF!2");
    requestBuilder.withConfirmPassword("ABCDEF!2");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_VALIDATION_ERROR,
        "Password must have at least "
            + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " lowercase character");
  }

  @Test
  public void createUserRequest_ShouldFail_PasswordAndConfirmPasswordMismatch() {
    requestBuilder.withPassword("ABCDEf!2");
    requestBuilder.withConfirmPassword("ABCDEfG!2");

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_MISMATCH,
        "Password and confirm password does not match");
  }

  @Test
  public void createUser_ShouldFail_RoleNull() {
    requestBuilder.withRole(null);

    final ErrorInfo responseErr = callCreateUserPutError(HttpStatus.LENGTH_REQUIRED, requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.ROLE_TYPE_NULL,
        "Role type may not be null");
  }

  @Test
  public void updateUser_ShouldWork_UpdateUserEmailAndUsername() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withEmail("hey@hey.com");
    reqUpdate.withUserName("MyNameIs");

    final String createdToken = response.getToken();
    final UserDto responseUpdatedDto = callUpdateUserPutOk(reqUpdate, response.getEmail(), createdToken);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users userUpdated = userRepository.findByEmail(responseUpdatedDto.getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(reqUpdate.build(), responseUpdatedDto,
        userUpdated, true);

    final UserDto userDto = callGetUserGetOk(responseUpdatedDto.getToken());
    TestCheckers.checkCorrectUserDtoVsUser(userDto, userUpdated, true);
  }

  @Test
  public void updateUser_ShouldWork_UpdateAllInformationOfUser() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withEmail("hey@hey.com");
    reqUpdate.withUserName("MyNameIs");
    reqUpdate.withFirstName("Nalle");
    reqUpdate.withLastName("Coffeeson");
    reqUpdate.withPassword("AbcdeF!2");
    reqUpdate.withConfirmPassword("AbcdeF!2");
    reqUpdate.withEnabled(true);
    reqUpdate.withRole(RoleTypes.MASTER_ADMIN_ROLE);

    final String createdToken = response.getToken();
    final UserDto responseUpdatedDto = callUpdateUserPutOk(reqUpdate, response.getEmail(), createdToken);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users userUpdated = userRepository.findByEmail(responseUpdatedDto.getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(reqUpdate.build(), responseUpdatedDto,
        userUpdated, true);

    final UserDto userDto = callGetUserGetOk(responseUpdatedDto.getToken());
    TestCheckers.checkCorrectUserDtoVsUser(userDto, userUpdated, true);
  }

  @Test
  public void updateUser_ShouldWork_UpdateAUserWithTheExactSameInformationThatIsAlreadyBeenSetDuringCreateUser() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withEmail(response.getEmail());
    reqUpdate.withUserName(response.getUserName());

    final String createdToken = response.getToken();
    final UserDto responseUpdatedDto = callUpdateUserPutOk(reqUpdate, response.getEmail(), createdToken);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users userUpdated = userRepository.findByEmail(responseUpdatedDto.getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(reqUpdate.build(), responseUpdatedDto,
        userUpdated, true);

    final UserDto userDto = callGetUserGetOk(responseUpdatedDto.getToken());
    TestCheckers.checkCorrectUserDtoVsUser(userDto, userUpdated, true);
  }

  @Test
  public void updateUser_ShouldWork_UpdatePassword() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withPassword("QwERTY1!");
    reqUpdate.withConfirmPassword("QwERTY1!");

    final String createdToken = response.getToken();
    final UserDto responseUpdatedDto = callUpdateUserPutOk(reqUpdate, response.getEmail(), createdToken);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users userUpdated = userRepository.findByEmail(responseUpdatedDto.getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(reqUpdate.build(), responseUpdatedDto,
        userUpdated, true);

    final UserDto userDto = callGetUserGetOk(responseUpdatedDto.getToken());
    TestCheckers.checkCorrectUserDtoVsUser(userDto, userUpdated, true);

    user = userRepository.findByEmail(requestBuilder.build().getEmail());
    testCheckers = new TestCheckers(passwordEncoder);
    testCheckers.checkHashedPassword(reqUpdate.build(), user);
  }

  @Test
  public void updateUser_ShouldWork_LastNameEmpty() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withLastName("");

    final String createdToken = response.getToken();
    final UserDto responseUpdatedDto = callUpdateUserPutOk(reqUpdate, response.getEmail(), createdToken);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users userUpdated = userRepository.findByEmail(responseUpdatedDto.getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(reqUpdate.build(), responseUpdatedDto,
        userUpdated, true);

    final UserDto userDto = callGetUserGetOk(responseUpdatedDto.getToken());
    TestCheckers.checkCorrectUserDtoVsUser(userDto, userUpdated, true);
  }

  @Test
  public void updateUser_ShouldFail_CorrectCurrentEmailButUserDoesNotExist() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    final CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);

    final String emailOfNonExistingUser = "myEmail@email.com";
    final ErrorInfo responseErrorUpdatedDto = callUpdateUserPutError(HttpStatus.BAD_REQUEST, reqUpdate,
        emailOfNonExistingUser, response.getToken());
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErrorUpdatedDto, ErrorCode.USER_NOT_FOUND,
        "UserServiceImpl :: updateUser :: User not found with currentEmail: "
            + emailOfNonExistingUser);
  }

  @Test
  public void updateUser_ShouldFail_RefreshTokenExpired() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final ZonedDateTime timeInThePast = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).minusDays(2);

    user.setEnabled(true);
    user.getVerificationToken().setRefreshToken(timeInThePast);
    userRepository.save(user);

    final CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);

    final ErrorInfo responseErrorUpdatedDto = callUpdateUserPutError(HttpStatus.NOT_ACCEPTABLE, reqUpdate,
        response.getEmail(), response.getToken());
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErrorUpdatedDto, ErrorCode.TOKEN_EXPIRED,
        "UserServiceImpl :: updateUser :: current time token expired at: " + timeInThePast);
  }

  @Test
  public void updateUser_ShouldFail_GivenTokenIsNotTheSameAsTheTokenFromUserInTheDatabase() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withEmail("hey@hey.com");
    reqUpdate.withUserName("MyNameIs");

    final String wrongToken = "TokenTokenTokenTokenTokenTokenWrong1";
    final ErrorInfo responseErrorUpdatedDto = callUpdateUserPutError(HttpStatus.BAD_REQUEST, reqUpdate,
        response.getEmail(), wrongToken);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErrorUpdatedDto, ErrorCode.TOKEN_MISMATCH,
        "UserServiceImpl :: updateUser :: User token and given token is not the same, given token: "
            + wrongToken + ", and user token: " + response.getToken());
  }

  @Test
  public void updateUser_ShouldFail_UpdateAUserWithAnEmailThatAlreadyExists() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("email2@email.com");
    request2.withUserName("Berra");

    final UserDto response1 = callCreateUserPutOk(requestBuilder);
    final UserDto response2 = callCreateUserPutOk(request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);

    Users user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    Users user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response1, user1,
        false);
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(request2.build(), response2, user2,
        false);

    user1.setEnabled(true);
    userRepository.save(user1);
    user2.setEnabled(true);
    userRepository.save(user2);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withEmail(requestBuilder.build().getEmail());
    reqUpdate.withUserName(response2.getUserName());

    final String createdToken2 = response2.getToken();
    final ErrorInfo responseErrorUpdatedDto = callUpdateUserPutError(HttpStatus.BAD_REQUEST, reqUpdate,
        response2.getEmail(), createdToken2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);
    TestCheckers.checkErrorResponse(responseErrorUpdatedDto, ErrorCode.EMAIL_ALREADY_EXISTS,
        "UserServiceImpl :: checkUserExistence :: User already exist with email: "
            + response1.getEmail());
  }

  @Test
  public void updateUser_ShouldFail_UpdateAUserWithAUsernameThatAlreadyExists() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("email2@email.com");
    request2.withUserName("Berra");

    final UserDto response1 = callCreateUserPutOk(requestBuilder);
    final UserDto response2 = callCreateUserPutOk(request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);

    Users user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    Users user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response1,
        user1, false);
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(request2.build(), response2, user2,
        false);

    user1.setEnabled(true);
    userRepository.save(user1);
    user2.setEnabled(true);
    userRepository.save(user2);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);
    reqUpdate.withEmail(response2.getEmail());
    reqUpdate.withUserName(requestBuilder.build().getUserName());

    final String createdToken2 = response2.getToken();
    final ErrorInfo responseErrorUpdatedDto = callUpdateUserPutError(HttpStatus.BAD_REQUEST, reqUpdate,
        response2.getEmail(), createdToken2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);
    TestCheckers.checkErrorResponse(responseErrorUpdatedDto, ErrorCode.USER_ALREADY_EXISTS,
        "UserServiceImpl :: checkUserExistence :: User already exist with username: "
            + response2.getUserName());
  }

  @Test
  public void updateUser_ShouldFail_InactiveUserAndTryToGetUserRole() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(false);

    final String createdToken = response.getToken();
    final UserDto responseUpdatedDto = callUpdateUserPutOk(reqUpdate, response.getEmail(), createdToken);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);
    Assertions.assertThat(responseUpdatedDto.getEnabled()).isEqualTo(false);

    final ErrorInfo responseErr = callGetUserGetError(HttpStatus.BAD_REQUEST, createdToken);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_ACTIVATED,
        "getUser :: User not activated with token: " + user.getVerificationToken().getToken());
  }

  @Test
  public void updateUser_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";

    final Users user = userRepository.findByEmail(response.getEmail());
    final CreateUserRequest.Builder reqUpdate = TestUsers.createCorrectUserRequestBuilder(true);

    final ErrorInfo responseErr = callUpdateUserPutError(HttpStatus.BAD_REQUEST, reqUpdate, response.getEmail(),
        token);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_MISMATCH,
        "UserServiceImpl :: updateUser :: User token and given token is not the same, given token: "
            + token + ", and user token: " + user.getVerificationToken().getToken());
  }

  @Test
  public void loginUser_ShouldWork_LoginUserWithCorrectEmail() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    user = userRepository.save(user);

    final String password = requestBuilder.build().getPassword();
    final GenericMessageResponse responseLogin = callLoginUserGetOk(user.getEmail(), password);
    Assertions.assertThat(responseLogin.getMessage()).isEqualTo("User logged in!");
    Assertions.assertThat(responseLogin.getToken()).isEqualTo(response.getToken());
  }

  @Test
  public void loginUser_ShouldFail_EmailOrUsernameIsNull() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.BAD_REQUEST, null,
        requestBuilder.build().getPassword());
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.VALIDATION_ERROR_REQUEST_PARAM,
        "loginUser :: User email/username are null or empty");
  }

  @Test
  public void loginUser_ShouldFail_EmailOrUsernameIsNullWithQuotationMarks() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.BAD_REQUEST, "null",
        requestBuilder.build().getPassword());
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.VALIDATION_ERROR_REQUEST_PARAM,
        "loginUser :: User email/username are null or empty");
  }

  @Test
  public void loginUser_ShouldWork_LoginUserWithCorrectUsername() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    user = userRepository.save(user);

    final String password = requestBuilder.build().getPassword();
    final GenericMessageResponse responseLogin = callLoginUserGetOk(user.getUserName(), password);
    Assertions.assertThat(responseLogin.getMessage()).isEqualTo("User logged in!");
    Assertions.assertThat(responseLogin.getToken()).isEqualTo(response.getToken());
  }

  @Test
  public void loginUser_ShouldWork_GenerateNewTokenBecauseOldTokenIsTooOld() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    user = userRepository.findByEmail(requestBuilder.build().getEmail());
    VerificationToken verificationToken = user.getVerificationToken();

    ZonedDateTime refreshToken = verificationToken.getRefreshToken();
    verificationToken.setRefreshToken(refreshToken.minusHours(8));
    userRepository.save(user);

    final String password = requestBuilder.build().getPassword();
    final GenericMessageResponse responseLogin = callLoginUserGetOk(user.getEmail(), password);
    Assertions.assertThat(responseLogin.getToken()).isNotEqualTo(response.getToken());
    Assertions.assertThat(responseLogin.getMessage()).isEqualTo("User logged in!");
  }

  @Test
  public void loginUser_ShouldFail_UserNotActivated() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user = userRepository.findByEmail(requestBuilder.build().getEmail());

    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.BAD_REQUEST, user.getEmail(),
        requestBuilder.build().getPassword());
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_ACTIVATED,
        "UserServiceImpl :: loginUser :: User not activated with email: hans@larsson.com");
  }

  @Test
  public void loginUser_ShouldFail_UserDeleted() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user = userRepository.findByEmail(requestBuilder.build().getEmail());
    user.setEnabled(true);
    user.setUserDeleted(true);
    userRepository.save(user);

    final String password = requestBuilder.build().getPassword();
    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.NOT_FOUND, user.getEmail(), password);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_DELETED,
        "UserServiceImpl :: updateUser :: User has been deleted so nothing can be performed on the "
            + "object, with: hans@larsson.com");
  }

  @Test
  public void loginUser_ShouldFail_EmailForUserDoesNotExist() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    final String nonExistingUserEmail = "hello@there.com";
    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.BAD_REQUEST, nonExistingUserEmail,
        requestBuilder.build().getPassword());
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "UserServiceImpl :: checkUserExistence :: User not found with email/username: "
            + nonExistingUserEmail);
  }

  @Test
  public void loginUser_ShouldFail_UsernameForUserDoesNotExist() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    final String nonExistingUsername = "MikeTheBike";
    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.BAD_REQUEST, nonExistingUsername,
        requestBuilder.build().getPassword());
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "UserServiceImpl :: checkUserExistence :: User not found with email/username: "
            + nonExistingUsername);
  }

  @Test
  public void loginUser_ShouldFail_NullPassword() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.BAD_REQUEST, user.getEmail(), null);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.VALIDATION_ERROR_REQUEST_PARAM,
        "loginUser :: User password is null or empty");
  }

  @Test
  public void loginUser_ShouldFail_NullPasswordWithInQuotationMarks() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.BAD_REQUEST, user.getEmail(), "null");
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.VALIDATION_ERROR_REQUEST_PARAM,
        "loginUser :: User password is null or empty");
  }

  @Test
  public void loginUser_ShouldFail_PasswordNotValid() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    final ErrorInfo responseErr = callLoginUserGetError(HttpStatus.NOT_ACCEPTABLE, user.getEmail(), "Hey");
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.PASSWORD_MISMATCH,
        "UserServiceImpl :: loginUser :: User request password does not match user password with "
            + "persistent user email: " + user.getEmail());
  }

  @Test
  public void getUser_ShouldWork_GetAUser() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    final String token = userRepository.findByEmail(user.getEmail()).getVerificationToken().getToken();
    final UserDto userDto = callGetUserGetOk(token);
    user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectUserDtoVsUser(userDto, user, true);
  }

  @Test
  public void getUser_ShouldWork_GetTwoUsers() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("lennart@lennartsson.org");
    request2.withUserName("Superman");

    final UserDto response1 = callCreateUserPutOk(requestBuilder);
    final UserDto response2 = callCreateUserPutOk(request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(2);

    Users user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    Users user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response1, user1,
        false);
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(request2.build(), response2, user2,
        false);

    user1.setEnabled(true);
    userRepository.save(user1);
    user2.setEnabled(true);
    userRepository.save(user2);

    final String token = userRepository.findByEmail(user1.getEmail()).getVerificationToken().getToken();
    final String token2 = userRepository.findByEmail(user2.getEmail()).getVerificationToken().getToken();
    final UserDto userDto1 = callGetUserGetOk(token);
    final UserDto userDto2 = callGetUserGetOk(token2);
    user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectUserDtoVsUser(userDto1, user1, true);
    TestCheckers.checkCorrectUserDtoVsUser(userDto2, user2, true);
  }

  @Test
  public void getUser_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";

    final ErrorInfo responseErr = callGetUserGetError(HttpStatus.BAD_REQUEST, token);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "getUser :: User not found with token: " + token);
  }

  @Test
  public void getUser_ShouldFail_TokenNull() {
    final ErrorInfo responseErr = callGetUserGetError(HttpStatus.BAD_REQUEST, null);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "getUser :: Token is null or empty with token: null");
  }

  @Test
  public void getUser_ShouldFail_TokenTooLong() {
    final String tokenTooLong = "tokentokentokentokentokentokentoken37";

    final ErrorInfo responseErr = callGetUserGetError(HttpStatus.BAD_REQUEST, tokenTooLong);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getUser :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooLong);
  }

  @Test
  public void getUser_ShouldFail_TokenTooShort() {
    final String tokenTooShort = "token";

    final ErrorInfo responseErr = callGetUserGetError(HttpStatus.BAD_REQUEST, tokenTooShort);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getUser :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooShort);
  }

  @Test
  public void getUser_ShouldFail_GetAUserThatHasNotBeenActivated() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    final ErrorInfo responseErr = callGetUserGetError(HttpStatus.BAD_REQUEST, response.getToken());
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_ACTIVATED,
        "getUser :: User not activated with token: " + response.getToken());
  }

  @Test
  public void getUser_ShouldFail_UserDeleted() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setUserDeleted(true);
    userRepository.save(user);

    final ErrorInfo responseErr = callGetUserGetError(HttpStatus.BAD_REQUEST, response.getToken());
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "getUser :: User not found with token: " + response.getToken());
  }

  @Test
  public void countAllUsers_ShouldWork_GetAllUsersCount() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("lennart@lennartsson.org");
    request2.withUserName("Superman");

    final UserDto response1 = callCreateUserPutOk(requestBuilder);
    final UserDto response2 = callCreateUserPutOk(request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(2);

    Users user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    Users user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response1, user1,
        false);
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(request2.build(), response2, user2,
        false);

    user1.setEnabled(true);
    userRepository.save(user1);
    user2.setEnabled(true);
    userRepository.save(user2);

    final GenericMessageResponse response = callCountAllUsersGetOk(response1.getToken());
    Assertions.assertThat(Long.valueOf(response.getMessage())).isEqualTo(2);
    Assertions.assertThat(response.getToken()).isNotEqualTo(response1.getToken());
  }

  @Test
  public void countAllUsers_ShouldWork_MakeTwoUsersAndDeleteOneShouldReturnUserCountOfOne() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("lennart@lennartsson.org");
    request2.withUserName("Superman");

    final UserDto response1 = callCreateUserPutOk(requestBuilder);
    final UserDto response2 = callCreateUserPutOk(request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(2);

    Users user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    Users user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response1, user1,
        false);
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(request2.build(), response2, user2,
        false);

    user1.setEnabled(true);
    userRepository.save(user1);
    user2.setEnabled(true);
    userRepository.save(user2);

    Users user = userRepository.findByEmail(request2.build().getEmail());
    user.setUserDeleted(true);
    userRepository.save(user);

    final GenericMessageResponse response = callCountAllUsersGetOk(response1.getToken());
    Assertions.assertThat(Long.valueOf(response.getMessage())).isEqualTo(1);
    Assertions.assertThat(response.getToken()).isNotEqualTo(response1.getToken());
  }

  @Test
  public void countAllUsers_ShouldWork_MakeTwoUsersButActivateOnlyOneShouldReturnUserCountOfOne() {
    CreateUserRequest.Builder request2 = TestUsers.createCorrectUserRequestBuilder();
    request2.withEmail("lennart@lennartsson.org");
    request2.withUserName("Superman");

    final UserDto response1 = callCreateUserPutOk(requestBuilder);
    final UserDto response2 = callCreateUserPutOk(request2);
    Assertions.assertThat(userRepository.findAll()).hasSize(2);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(2);

    Users user1 = userRepository.findByEmail(requestBuilder.build().getEmail());
    Users user2 = userRepository.findByEmail(request2.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response1, user1,
        false);
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(request2.build(), response2, user2,
        false);

    user1.setEnabled(true);
    userRepository.save(user1);

    final GenericMessageResponse response = callCountAllUsersGetOk(response1.getToken());
    Assertions.assertThat(Long.valueOf(response.getMessage())).isEqualTo(1);
    Assertions.assertThat(response.getToken()).isNotEqualTo(response1.getToken());
  }

  @Test
  public void countAllUsers_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";

    final ErrorInfo responseErr = callCountAllUsersGetError(HttpStatus.BAD_REQUEST, token);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "countAllUsers :: User not found with token: " + token);
  }

  @Test
  public void countAllUsers_ShouldFail_TokenNull() {
    final ErrorInfo responseErr = callCountAllUsersGetError(HttpStatus.BAD_REQUEST, null);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "countAllUsers :: Token is null or empty with token: null");
  }

  @Test
  public void countAllUsers_ShouldFail_TokenTooLong() {
    final String tokenTooLong = "tokentokentokentokentokentokentoken37";

    final ErrorInfo responseErr = callCountAllUsersGetError(HttpStatus.BAD_REQUEST, tokenTooLong);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "countAllUsers :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooLong);
  }

  @Test
  public void countAllUsers_ShouldFail_TokenTooShort() {
    final String tokenTooShort = "token";

    final ErrorInfo responseErr = callCountAllUsersGetError(HttpStatus.BAD_REQUEST, tokenTooShort);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "countAllUsers :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooShort);
  }

  @Test
  public void deleteUser_ShouldWork_DeleteACorrectlyCreatedAndActivatedUser() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.setEnabled(true);
    userRepository.save(user);

    final GenericMessageResponse responseDelete = callDeleteUserGetOk(response.getToken());
    Assertions.assertThat(responseDelete.getMessage()).isEqualTo("User deleted");
    Assertions.assertThat(responseDelete.getToken()).isNotEqualTo(response.getToken());

    user = userRepository.findByEmail(requestBuilder.build().getEmail());
    Assertions.assertThat(user.getUserDeleted()).isEqualTo(true);
  }

  @Test
  public void deleteUser_ShouldWork_DeleteACorrectlyCreatedButNotActivatedUser() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    final GenericMessageResponse responseDelete = callDeleteUserGetOk(response.getToken());
    Assertions.assertThat(responseDelete.getMessage()).isEqualTo("User deleted");
    Assertions.assertThat(responseDelete.getToken()).isNotEqualTo(response.getToken());

    user = userRepository.findByEmail(requestBuilder.build().getEmail());
    Assertions.assertThat(user.getUserDeleted()).isEqualTo(true);
  }

  @Test
  public void deleteUser_ShouldFail_TokenExpired() {
    final UserDto response = callCreateUserPutOk(requestBuilder);
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);

    Users user = userRepository.findByEmail(requestBuilder.build().getEmail());
    TestCheckers.checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(requestBuilder.build(), response, user,
        false);

    user.getVerificationToken().setRefreshToken(user.getVerificationToken().getRefreshToken().minusDays(8));
    user.setEnabled(true);
    user = userRepository.save(user);

    final ZonedDateTime expiredTime = user.getVerificationToken().getRefreshToken();
    final ErrorInfo responseErr = callDeleteUserGetError(HttpStatus.NOT_ACCEPTABLE, response.getToken());
    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_EXPIRED,
        "deleteUser :: User with email: hans@larsson.com, token expired at: " + expiredTime
            + ". User must login again");
  }

  @Test
  public void deleteUser_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";

    final ErrorInfo responseErr = callDeleteUserGetError(HttpStatus.BAD_REQUEST, token);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "deleteUser :: User not found with token: " + token);
  }

  @Test
  public void deleteUser_ShouldFail_TokenNull() {
    final ErrorInfo responseErr = callDeleteUserGetError(HttpStatus.BAD_REQUEST, null);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "deleteUser :: Token is null or empty with token: null");
  }

  @Test
  public void deleteUser_ShouldFail_TokenTooLong() {
    final String tokenTooLong = "tokentokentokentokentokentokentoken37";

    final ErrorInfo responseErr = callDeleteUserGetError(HttpStatus.BAD_REQUEST, tokenTooLong);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "deleteUser :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooLong);
  }

  @Test
  public void deleteUser_ShouldFail_TokenTooShort() {
    final String tokenTooShort = "token";

    final ErrorInfo responseErr = callDeleteUserGetError(HttpStatus.BAD_REQUEST, tokenTooShort);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "deleteUser :: Token must be " + Constraint.MAX_MIN_TOKEN + " characters with token: "
            + tokenTooShort);
  }

  private UserDto callCreateUserPutOk(final CreateUserRequest.Builder request) {
    return httpPut(USER_URL + "/v1/upsertUser", HttpStatus.OK, request.build(), UserDto.class);
  }

  private ErrorInfo callCreateUserPutError(final HttpStatus expectedStatus, final CreateUserRequest.Builder request) {
    return httpPutError(USER_URL + "/v1/upsertUser", expectedStatus, request.build(), ErrorInfo.class);
  }

  private UserDto callUpdateUserPutOk(final CreateUserRequest.Builder updateRequest, final String email,
      final String token) {
    return httpPut(USER_URL + "/v1/upsertUser/" + email + "/" + token, HttpStatus.OK, updateRequest.build(),
        UserDto.class);
  }

  private ErrorInfo callUpdateUserPutError(final HttpStatus expectedStatus,
      final CreateUserRequest.Builder updateRequest, final String email, final String token) {
    return httpPutError(USER_URL + "/v1/upsertUser/" + email + "/" + token, expectedStatus, updateRequest.build(),
        ErrorInfo.class);
  }

  private GenericMessageResponse callLoginUserGetOk(final String emailOrUsername, final String password) {
    return httpGet(USER_URL + "/v1/login/" + emailOrUsername + "/" + password, HttpStatus.OK,
        GenericMessageResponse.class);
  }

  private ErrorInfo callLoginUserGetError(final HttpStatus expectedStatus, final String emailOrUsername,
      final String password) {
    return httpGetError(USER_URL + "/v1/login/" + emailOrUsername + "/" + password, expectedStatus,
        ErrorInfo.class);
  }

  private UserDto callGetUserGetOk(final String token) {
    return httpGet(USER_URL + "/v1/getUser/" + token, HttpStatus.OK, UserDto.class);
  }

  private ErrorInfo callGetUserGetError(final HttpStatus expectedStatus, final String token) {
    return httpGetError(USER_URL + "/v1/getUser/" + token, expectedStatus, ErrorInfo.class);
  }

  private GenericMessageResponse callCountAllUsersGetOk(final String token) {
    return httpGet(USER_URL + "/v1/countNumbers/" + token, HttpStatus.OK, GenericMessageResponse.class);
  }

  private ErrorInfo callCountAllUsersGetError(final HttpStatus expectedStatus, final String token) {
    return httpGetError(USER_URL + "/v1/countNumbers/" + token, expectedStatus, ErrorInfo.class);
  }

  private GenericMessageResponse callDeleteUserGetOk(final String token) {
    return httpGet(USER_URL + "/v1/deleteUser/" + token, HttpStatus.OK, GenericMessageResponse.class);
  }

  private ErrorInfo callDeleteUserGetError(final HttpStatus expectedStatus, final String token) {
    return httpGetError(USER_URL + "/v1/deleteUser/" + token, expectedStatus, ErrorInfo.class);
  }
}
