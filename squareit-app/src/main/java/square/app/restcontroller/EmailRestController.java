package square.app.restcontroller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.headersdefinition.HeadersDefinition;
import square.api.domain.models.GenericMessageResponse;

import square.app.constants.EmailTypes;
import square.app.constants.TokenRequestType;
import square.app.domain.jpa.Users;
import square.app.errorhandling.ErrorHandling;
import square.app.exceptions.EmailException;
import square.app.exceptions.EmailLengthException;
import square.app.exceptions.ObjectNotFoundException;
import square.app.exceptions.UserAlreadyActivatedException;
import square.app.restcontroller.baserestcontroller.BaseRestController;
import square.app.restcontroller.httpresponsecodes.HttpResponseCodes;
import square.app.service.EmailService;
import square.app.service.UserService;
import square.app.validator.TokenValidator;

@RestController
@RequestMapping("/rest/email")
public class EmailRestController extends BaseRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailRestController.class);

  private final EmailService emailService;

  private final UserService userService;

  @Autowired
  public EmailRestController(final UserService userService, final EmailService emailService) {
    this.userService = userService;
    this.emailService = emailService;
  }

  /**
   * Send registration email rest-service.
   *
   * @param token user token
   * @return generic message of registration mail sent success
   */
  @ApiOperation(value = "registrationEmail", notes = "Send registration email to user email.")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "User was sent registration mail"),
      @ApiResponse(code = HttpResponseCodes.BAD_REQUEST, message = "Malformed request token parameter"),
      @ApiResponse(code = HttpResponseCodes.NOT_FOUND, message = "User not found"),
      @ApiResponse(code = HttpResponseCodes.UNSUPPORTED_MEDIA_TYPE, message = "Unsupported media type"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/registrationEmail/{token}")
  public GenericMessageResponse registrationEmail(@PathVariable("token") final String token) {
    try {
      LOGGER.info(">> registrationEmail >>");
      LOGGER.info("Token: {}", token);

      ErrorHandling.errorHandlingToken(token, "registrationEmail");
      final Users user = userService.getUserByToken(token);

      if (user == null) {
        ErrorHandling.errorHandlingNonExistingUser(token, "token", "registrationEmail");
      }

      assert user != null;
      ErrorHandling.errorHandlingDeletedUser(user, user.getEmail(), "registrationEmail");

      if (user.getEnabled()) {
        String userEmail = user.getEmail();
        LOGGER.debug("-- resendRegistrationEmail :: User already registered with email: {} --", userEmail);
        throw new UserAlreadyActivatedException("resendRegistrationEmail :: User already registered with email: "
            + userEmail);
      }

      emailService.createRegistrationMail(user, EmailTypes.NEW_ACCOUNT);

      LOGGER.info("<< registrationEmail <<");
      return new GenericMessageResponse().withMessage("Successfully mailed user", token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot send registration mail with token: {}. Exception {}.", token, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when creating registration mail to user with token: " + token + ".", e);
      throw e;
    }
  }

  /**
   * Resend registration email rest-service.
   *
   * @param email user email
   * @return generic message of resend registration success
   */
  @ApiOperation(value = "resendRegistrationEmail", notes = "Resend registration email to user email.")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "User email was resent"),
      @ApiResponse(code = HttpResponseCodes.BAD_REQUEST, message = "Malformed request object"),
      @ApiResponse(code = HttpResponseCodes.NOT_FOUND, message = "User not found"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/resendRegistrationEmail/{email}")
  public GenericMessageResponse resendRegistrationEmail(@PathVariable("email") final String email) {
    try {
      LOGGER.info(">> resendRegistrationEmail >>");
      LOGGER.info("Email: {}", email);

      ErrorHandling.errorHandlingEmail(email, "resendRegistrationEmail");
      Users user = userService.getUserByEmail(email);
      ErrorHandling.errorHandlingDeletedUser(user, user.getEmail(), "resendRegistrationEmail");

      if (user.getEnabled()) {
        String userEmail = user.getEmail();
        LOGGER.debug("-- resendRegistrationEmail :: User already registered with email: {} --", userEmail);
        throw new UserAlreadyActivatedException("resendRegistrationEmail :: User already registered with email: "
            + userEmail);
      }

      user = userService.generateToken(user, TokenRequestType.CREATE_TOKEN);
      emailService.createRegistrationMail(user, EmailTypes.NEW_ACCOUNT_RESEND);

      String token = user.getVerificationToken().getToken();

      LOGGER.info("User: {}, new token: {}", user.toString(), token);
      LOGGER.info("<< resendRegistrationEmail <<");
      return new GenericMessageResponse().withMessage("Successfully re-mailed user", token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot resend registration mail to mail: {}. Exception {}.", email, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when creating reregistration mail to user with mail: " + email + ".", e);
      throw e;
    }
  }

  /**
   * Confirm user email rest-service.
   *
   * @param token user token
   * @return generic message of registration success
   */
  @ApiOperation(value = "confirmRegistration", notes = "User clicked on the link in email and is activated.")
  @ApiResponses(value = {
      @ApiResponse(code = HttpResponseCodes.OK, message = "User confirmed registered OK"),
      @ApiResponse(code = HttpResponseCodes.CREATED, message = "User confirmed registered"),
      @ApiResponse(code = HttpResponseCodes.BAD_REQUEST, message = "Malformed token"),
      @ApiResponse(code = HttpResponseCodes.NOT_FOUND, message = "User token not valid or token expired"),
      @ApiResponse(code = HttpResponseCodes.INTERNAL_SERVER_ERROR, message = "Internal server error")})
  @GetMapping(value = "/v1/confirmRegistration/{token}")
  public GenericMessageResponse confirmRegistration(@PathVariable("token") String token) {
    try {
      LOGGER.info(">> confirmRegistration >>");
      LOGGER.info("Token: {}", token);

      ErrorHandling.errorHandlingToken(token, "confirmRegistration");
      Users user = userService.getUserByToken(token);

      if (user == null) {
        ErrorHandling.errorHandlingNonExistingUser(token, "token", "confirmRegistration");
      }

      assert user != null;
      ErrorHandling.errorHandlingDeletedUser(user, user.getEmail(), "confirmRegistration");

      boolean validToken = TokenValidator.isValidExpirationToken(user.getVerificationToken().getExpiryDate());
      if (!validToken) {
        final String userEmail = user.getEmail();
        final ZonedDateTime expiryDate = user.getVerificationToken().getExpiryDate();
        resendRegistrationEmail(userEmail);
        LOGGER.debug("-- confirmRegistration :: resendRegistrationEmail for email: {} , and token expired at {} --",
            userEmail, expiryDate);
        return new GenericMessageResponse().withMessage("Unsuccessful. User account not activated. New mail "
            + "for verification sent", token);
      } else {
        userService.enableConfirmedUser(user);
        final Users confirmedUser = userService.getUserByToken(token);
        emailService.createRegistrationMail(confirmedUser, EmailTypes.NEW_ACCOUNT_CONFIRMED);
      }

      token = userService.generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();

      LOGGER.info("New token: {}", token);
      LOGGER.info("<< confirmRegistration <<");
      return new GenericMessageResponse().withMessage("Successfully activated user account", token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot find user by token with token: {}. Exception {}.", token, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when finding user with token: " + token + ".", e);
      throw e;
    }
  }

  /**
   * EmailLengthException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({EmailLengthException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalEmailLengthException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.EMAIL_LENGTH_MISMATCH)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * EmailException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({EmailException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalEmailException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.EMAIL_LENGTH_MISMATCH)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }
}
