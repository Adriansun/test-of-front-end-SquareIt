package square.app.restcontroller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import square.api.domain.constants.Constraint;
import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.headersdefinition.HeadersDefinition;
import square.api.domain.models.GenericMessageResponse;
import square.api.domain.models.user.CreateUserRequest;
import square.api.domain.models.user.UserDto;

import square.app.constants.TokenRequestType;
import square.app.constants.UserRequestType;
import square.app.converters.UserConverter;
import square.app.domain.jpa.Users;
import square.app.errorhandling.ErrorHandling;
import square.app.exceptions.ObjectNotFoundException;
import square.app.exceptions.PasswordMismatchException;
import square.app.restcontroller.baserestcontroller.BaseRestController;
import square.app.restcontroller.httpresponsecodes.HttpResponseCodes;
import square.app.service.UserService;

@RestController
@RequestMapping("/rest/user")
public class UserRestController extends BaseRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

  private static final String CHARACTERS = " characters";

  private final UserService userService;

  /**
   * UserRestController rest service for handling users.
   *
   * @param userService userService
   */
  @Autowired
  public UserRestController(final UserService userService) {
    this.userService = userService;
  }

  /**
   * Upsert a user rest-service.
   *
   * @param request   object of (new) user data
   * @param oldEmail  oldEmail
   * @param userToken user token
   * @return UserDto
   */
  @ApiOperation(value = "upsertUser", notes = "Upsert a user. (Create / Update user)")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "User created or updated"),
      @ApiResponse(code = HttpResponseCodes.BAD_REQUEST, message = "Malformed request object"),
      @ApiResponse(code = HttpResponseCodes.UNSUPPORTED_MEDIA_TYPE, message = "Unsupported media type"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE,
      value = {"/v1/upsertUser", "/v1/upsertUser/{email}/{token}"})
  @ResponseBody
  public UserDto upsertUser(@RequestBody @Valid final CreateUserRequest request,
      @PathVariable(value = "email", required = false) final Optional<String> oldEmail,
      @PathVariable(value = "token", required = false) final Optional<String> userToken) {
    try {
      LOGGER.info(">> upsertUser >>");
      LOGGER.info("User request: {}", request.toString());

      String currentEmail = null;
      String token = null;

      if (oldEmail.isPresent() && userToken.isPresent()) {
        currentEmail = oldEmail.get();
        token = userToken.get();
      }

      boolean update = currentEmail != null && !currentEmail.isEmpty() && !token.isEmpty();
      if (update) {
        ErrorHandling.errorHandlingEmail(currentEmail, "upsertUser");
        ErrorHandling.errorHandlingToken(token, "upsertUser");
      }

      final Users user = userService.upsertUser(request, currentEmail, token,
          update ? UserRequestType.UPDATE_USER : UserRequestType.CREATE_USER);

      LOGGER.info("User: {}, VerificationToken: {}", user.toString(), user.getVerificationToken().toString());
      LOGGER.info("<< upsertUser <<");
      return UserConverter.toUserDto(user, user.getVerificationToken().getToken());
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot create user: {}. Exception {}.", request.getEmail(), e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when creating user: " + request.getEmail() + ".", e);
      throw e;
    }
  }

  /**
   * Login user rest-service.
   *
   * @param emailOrUserName user email or username
   * @param password        password
   * @return token
   */
  @ApiOperation(value = "loginUser", notes = "Login a user.")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "User is logged in"),
      @ApiResponse(code = HttpResponseCodes.BAD_REQUEST, message = "Malformed request token parameter"),
      @ApiResponse(code = HttpResponseCodes.NOT_FOUND, message = "User not found"),
      @ApiResponse(code = HttpResponseCodes.NOT_ACCEPTABLE, message = "Not acceptable password"),
      @ApiResponse(code = HttpResponseCodes.UNSUPPORTED_MEDIA_TYPE, message = "Unsupported media type"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/login/{emailOrUsername}/{password}")
  public GenericMessageResponse loginUser(@PathVariable("emailOrUsername") final String emailOrUserName,
      @PathVariable("password") final String password) {
    try {
      LOGGER.info(">> loginUser >>");
      LOGGER.info("Email or username: {}", emailOrUserName);

      if (emailOrUserName == null || emailOrUserName.isEmpty() || emailOrUserName.equals("null")) {
        LOGGER.debug("-- loginUser :: User email/username are null or empty --");
        throw new IllegalArgumentException("loginUser :: User email/username are null or empty");
      } else if (password == null || password.isEmpty() || password.equals("null")) {
        LOGGER.debug("-- loginUser :: User password is null or empty --");
        throw new IllegalArgumentException("loginUser :: User password is null or empty");
      }

      final String token = userService.loginUser(emailOrUserName, password);

      LOGGER.info("User new token: {}", token);
      LOGGER.info("<< loginUser <<");
      return new GenericMessageResponse().withMessage("User logged in!", token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Could not loginUser user with email or username: {}. Exception {}.", emailOrUserName, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when logging in user with email or username: {}. Exception {}.", emailOrUserName + ".", e);
      throw e;
    }
  }

  /**
   * Get user by token rest-service.
   *
   * @param token user token
   * @return user object
   */
  @ApiOperation(value = "getUser", notes = "Get user by token.")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "User returned"),
      @ApiResponse(code = HttpResponseCodes.BAD_REQUEST, message = "Malformed token"),
      @ApiResponse(code = HttpResponseCodes.NOT_FOUND, message = "User token not valid or token expired"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @GetMapping(value = "/v1/getUser/{token}")
  public UserDto getUser(@PathVariable("token") final String token) {
    try {
      LOGGER.info(">> getUser >>");
      LOGGER.info("User token: {}", token);

      final Users user = checkUser("getUser", token, true);

      LOGGER.info("User: {}", user.toString());
      LOGGER.info("<< getUser <<");
      return UserConverter.toUserDto(user, user.getVerificationToken().getToken());
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot find user by token with token: {}. Exception {}.", token, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when finding user with token: " + token + ".", e);
      throw e;
    }
  }

  /**
   * *
   * Count all users rest-service.
   *
   * @return number of users
   */
  @ApiOperation(value = "countAllUsers", notes = "Count all active users.")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "Count done"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/countNumbers/{token}")
  @ResponseBody
  public GenericMessageResponse countAllUsers(@PathVariable("token") String token) {
    try {
      LOGGER.info(">> countAllUsers >>");
      LOGGER.info("Requesting token of user: {}", token);

      final Users user = checkUser("countAllUsers", token, true);
      final long count = userService.countAllUsers();
      token = userService.generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();

      LOGGER.info("User count: {}, token: {}", count, token);
      LOGGER.info("<< countAllUsers <<");
      return new GenericMessageResponse().withMessage(String.valueOf(count), token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot count the number of users. Exception {}", e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when counting all rows of users.", e);
      throw e;
    }
  }

  /**
   * Delete a user rest-service.
   *
   * @param token user token
   * @return generic success response
   */
  @ApiOperation(value = "deleteUser", notes = "Delete a user.")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "User deleted"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/deleteUser/{token}")
  @ResponseBody
  public GenericMessageResponse deleteUser(@PathVariable("token") final String token) {
    try {//todo DeleteMapping inte Getmapping??
      LOGGER.info(">> deleteUser >>");
      LOGGER.info("User to delete token: {}", token);

      final Users user = checkUser("deleteUser", token, false);
      userService.deleteUser(user);

      LOGGER.info("User deleted");
      LOGGER.info("<< deleteUser <<");
      return new GenericMessageResponse().withMessage("User deleted", null);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot count the number of users. Exception {}", e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when counting all rows of users.", e);
      throw e;
    }
  }

  private Users checkUser(final String methodName, final String token, final boolean handleDeactivatedUser) {
    ErrorHandling.errorHandlingToken(token, methodName);
    Users user = userService.getUserByToken(token);
    ErrorHandling.userValidationChecks(user, handleDeactivatedUser, token, methodName);

    return user;
  }

  /**
   * PasswordMismatchException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({PasswordMismatchException.class})
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ErrorInfo handleIllegalPasswordMismatchException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.PASSWORD_MISMATCH)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * MethodArgumentNotValidException catches annotation errors in objects.
   *
   * @param ex ex
   * @return return
   */
  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.LENGTH_REQUIRED)
  public ErrorInfo handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
    String errorDescription = Objects.requireNonNull(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    ErrorCode errorCode;

    errorCode = errorCodeUsername(errorDescription);

    if (errorCode == null) {
      errorCode = errorCodeFirstName(errorDescription);
    }

    if (errorCode == null) {
      errorCode = errorCodeLastName(errorDescription);
    }

    if (errorCode == null) {
      errorCode = errorCodeEmail(errorDescription);
    }

    if (errorCode == null) {
      errorCode = errorCodePassword(errorDescription);
    }

    if (errorCode == null) {
      errorCode = errorCodeRoleType(errorDescription);
    }

    return ErrorInfo.newErrorInfo()
        .withErrorCode(errorCode)
        .withErrorDescription(errorDescription)
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  private ErrorCode errorCodeUsername(final String errorDescription) {
    ErrorCode errorCode = null;

    if ("Username may not be null or empty".equals(errorDescription)) {
      errorCode = ErrorCode.USERNAME_NULL_OR_EMPTY;
    } else if (("Username may not be more than " + Constraint.MAX_USERNAME + CHARACTERS).equals(errorDescription)
        || (("Username may not be less than " + Constraint.MIN_USERNAME + CHARACTERS).equals(errorDescription))) {
      errorCode = ErrorCode.USERNAME_LENGTH_MISMATCH;
    } else if (("Username may only use lowercase lowercase / uppercase letters, numbers 0-9, underscores and "
        + "whitespaces").equals(errorDescription)) {
      errorCode = ErrorCode.USERNAME_VALIDATION_ERROR;
    }

    return errorCode;
  }

  private ErrorCode errorCodeFirstName(final String errorDescription) {
    ErrorCode errorCode = null;

    if ("First name may not be null, empty, or contain one whitespace".equals(errorDescription)) {
      errorCode = ErrorCode.USER_FIRST_NAME_NULL_OR_EMPTY_OR_BLANK_SPACE;
    } else if (("First name may not be more than " + Constraint.MAX_FIRSTNAME + CHARACTERS)
        .equals(errorDescription)) {
      errorCode = ErrorCode.USER_FIRST_NAME_LENGTH_MISMATCH;
    }

    return errorCode;
  }

  private ErrorCode errorCodeLastName(final String errorDescription) {
    ErrorCode errorCode = null;

    if ("Last name may not be null".equals(errorDescription)) {
      errorCode = ErrorCode.USER_LAST_NAME_NULL;
    } else if (("Last name may not be more than " + Constraint.MAX_LASTNAME + CHARACTERS).equals(errorDescription)) {
      errorCode = ErrorCode.USER_LAST_NAME_TOO_LONG;
    }

    return errorCode;
  }

  private ErrorCode errorCodeEmail(final String errorDescription) {
    ErrorCode errorCode = null;

    if ("Email may not be null or empty".equals(errorDescription)) {
      errorCode = ErrorCode.EMAIL_NULL_OR_EMPTY;
    } else if (("Email may not be more than " + Constraint.MAX_EMAIL + CHARACTERS).equals(errorDescription)
        || ("Email may not be less than " + Constraint.MIN_EMAIL + CHARACTERS).equals(errorDescription)) {
      errorCode = ErrorCode.EMAIL_LENGTH_MISMATCH;
    } else if ("Email does not follow pattern restrictions".equals(errorDescription)) {
      errorCode = ErrorCode.EMAIL_VALIDATION_ERROR;
    }

    return errorCode;
  }

  private ErrorCode errorCodePassword(final String errorDescription) {
    ErrorCode errorCode = null;
    final String PW_AT_LEAST = "Password must have at least ";

    if ("Password may not be null or empty".equals(errorDescription)) {
      errorCode = ErrorCode.PASSWORD_NULL_OR_EMPTY;
    } else if ("Password and confirm password does not match".equals(errorDescription)) {
      errorCode = ErrorCode.PASSWORD_MISMATCH;
    } else if (("Password must be at least " + Constraint.MIN_PASSWORD + CHARACTERS).equals(errorDescription)
        || ("Password may not be more than " + Constraint.MAX_PASSWORD + CHARACTERS).equals(errorDescription)) {
      errorCode = ErrorCode.PASSWORD_LENGTH_MISMATCH;
    } else if ((PW_AT_LEAST + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " special character")
        .equals(errorDescription)) {
      errorCode = ErrorCode.PASSWORD_VALIDATION_ERROR;
    } else if ((PW_AT_LEAST + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " number")
        .equals(errorDescription)) {
      errorCode = ErrorCode.PASSWORD_VALIDATION_ERROR;
    } else if ((PW_AT_LEAST + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " UPPERCASE character")
        .equals(errorDescription)) {
      errorCode = ErrorCode.PASSWORD_VALIDATION_ERROR;
    } else if ((PW_AT_LEAST + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " lowercase character")
        .equals(errorDescription)) {
      errorCode = ErrorCode.PASSWORD_VALIDATION_ERROR;
    }

    return errorCode;
  }

  private ErrorCode errorCodeRoleType(final String errorDescription) {
    ErrorCode errorCode = null;

    if ("Role type may not be null".equals(errorDescription)) {
      errorCode = ErrorCode.ROLE_TYPE_NULL;
    } else if ("Not a valid roleType".equals(errorDescription)) {
      errorCode = ErrorCode.ROLE_TYPE_NOT_VALID;
    }

    return errorCode;
  }
}
