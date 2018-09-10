package square.app.errorhandling;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import square.api.domain.constants.Constraint;

import square.app.domain.jpa.Number;
import square.app.domain.jpa.Users;
import square.app.exceptions.EmailException;
import square.app.exceptions.EmailExistsException;
import square.app.exceptions.EmailLengthException;
import square.app.exceptions.NumberException;
import square.app.exceptions.NumberLengthException;
import square.app.exceptions.NumberNotFoundException;
import square.app.exceptions.TokenException;
import square.app.exceptions.TokenExpiredException;
import square.app.exceptions.TokenLengthException;
import square.app.exceptions.UserDeletedException;
import square.app.exceptions.UserExistsException;
import square.app.exceptions.UserNotActivatedException;
import square.app.exceptions.UserNotFoundException;
import square.app.validator.TokenValidator;

public class ErrorHandling {

  private ErrorHandling() {
    throw new IllegalStateException("ErrorHandling :: Cannot be instantiated");
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandling.class);

  /**
   * Validation of user existence and enabled state.
   *
   * @param user                  user
   * @param handleDeactivatedUser check is user has been activated
   * @param token                 user token
   * @param methodName            method name requesting the check
   */
  public static void userValidationChecks(final Users user, final boolean handleDeactivatedUser, final String token,
      final String methodName) {
    if (user == null) {
      errorHandlingNonExistingUser(token, "token", methodName);
    }

    errorHandlingDeletedUser(user, "token", methodName);

    if (handleDeactivatedUser) {
      errorHandlingDeactivatedUser(user, token, "token", methodName);
    }

    final ZonedDateTime refreshToken = user.getVerificationToken().getRefreshToken();
    final String email = user.getEmail();

    final boolean validToken = TokenValidator.isValidLoginToken(refreshToken);
    if (!validToken) {
      LOGGER.debug("-- {} :: for email: {}, and token expired at {} --", methodName, email, refreshToken);
      throw new TokenExpiredException(methodName + " :: User with email: " + email + ", token expired at: "
          + refreshToken + ". User must login again");
    }
  }

  /**
   * *
   * Handling user active state.
   *
   * @param user       user
   * @param fieldValue token or email
   * @param fieldType  type of field
   * @param methodText method name
   */
  public static void errorHandlingDeactivatedUser(final Users user, final String fieldValue, final String fieldType,
      final String methodText) {
    if (!user.getEnabled()) {
      LOGGER.debug("-- {} :: User not activated with {}: {} --", methodText, fieldType, fieldValue);
      throw new UserNotActivatedException(methodText + " :: User not activated with " + fieldType + ": " + fieldValue);
    }
  }

  /**
   * Handling user deleted state.
   *
   * @param user       user
   * @param methodText method name
   */
  public static void errorHandlingDeletedUser(final Users user, final String fieldValue, final String methodText) {
    if (user.getUserDeleted()) {
      LOGGER.debug("-- {} :: User has been deleted so nothing can be performed on the object, with: {} --", methodText,
          fieldValue);
      throw new UserDeletedException(methodText + " :: User has been deleted so nothing can be performed on the "
          + "object, with: " + fieldValue);
    }
  }

  /**
   * Handling user objects that do not exist.
   *
   * @param value      token or email
   * @param methodName method name
   */
  public static void errorHandlingNonExistingUser(final String value, final String fieldName,
      final String methodName) {
    LOGGER.debug("-- {} :: User not found with {}: {} --", methodName, fieldName, value);
    throw new UserNotFoundException(methodName + " :: User not found with " + fieldName + ": " + value);
  }

  /**
   * Handling user objects that do exist.
   *
   * @param value     value of the field
   * @param fieldType type of field
   * @param errorText error text
   */
  public static void errorHandlingExistingUser(final String value, final String fieldType, final String errorText) {
    LOGGER.debug("-- UserServiceImpl :: checkUserExistence :: {} {} --", errorText, value);

    if (fieldType.equals("email")) {
      throw new EmailExistsException("UserServiceImpl :: checkUserExistence :: " + errorText + value);
    } else {
      throw new UserExistsException("UserServiceImpl :: checkUserExistence :: " + errorText + value);
    }
  }

  /**
   * Handling token errors.
   *
   * @param value      token
   * @param methodName method name
   */
  public static void errorHandlingToken(final String value, final String methodName) {
    if (value == null || value.isEmpty() || value.equals("null")) {
      LOGGER.debug("-- {} :: Token is null or empty with token: {} --", methodName, value);
      throw new TokenException(methodName + " :: Token is null or empty with token: " + value);
    }

    if (value.length() > Constraint.MAX_MIN_TOKEN || value.length() < Constraint.MAX_MIN_TOKEN) {
      LOGGER.debug("-- {} :: Token must be {} characters with token: {} --", methodName, Constraint.MAX_MIN_TOKEN,
          value);
      throw new TokenLengthException(methodName + " :: Token must be " + Constraint.MAX_MIN_TOKEN
          + " characters with token: " + value);
    }
  }

  /**
   * Handling email errors.
   *
   * @param value      email
   * @param methodName method name
   */
  public static void errorHandlingEmail(final String value, final String methodName) {
    if (value == null || value.isEmpty() || value.equals("null")) {
      LOGGER.debug("-- {} :: Email is null or empty with email: {} --", methodName, value);
      throw new EmailException(methodName + " :: Email is null or empty with email: " + value);
    }

    if (value.length() > Constraint.MAX_EMAIL) {
      LOGGER.debug("-- {} :: Email must at most be {} characters with email: {} --", methodName, Constraint.MAX_EMAIL,
          value);
      throw new EmailLengthException(methodName + " :: Email must at most be " + Constraint.MAX_EMAIL
          + " characters with email: " + value);
    }

    if (value.length() < Constraint.MIN_EMAIL) {
      LOGGER.debug("-- {} :: Email must at least be {} characters with email: {} --", methodName, Constraint.MIN_EMAIL,
          value);
      throw new EmailLengthException(methodName + " :: Email must at least be " + Constraint.MIN_EMAIL
          + " characters with email: " + value);
    }
  }

  /**
   * Handling integer errors.
   *
   * @param value      value of long
   * @param token      user token
   * @param numberType number or numberId field
   * @param methodName method name
   */
  public static void errorHandlingIntLongTypeValue(final Long value, final String token, final String numberType,
      final String methodName, final boolean isLong) {
    if (value == null) {
      LOGGER.debug("-- {} :: {} is null with token: {} --", methodName, numberType, token);
      throw new NumberException(methodName + " :: " + numberType + " is null with token: " + token);
    }

    if (isLong) {
      if (value > Long.MAX_VALUE) {
        LOGGER.debug("-- {} :: {} must at most be {} characters with token: {} --", methodName, numberType,
            Long.MAX_VALUE, token);
        throw new NumberLengthException(methodName + " :: " + numberType + " must at most be " + Long.MAX_VALUE
            + " characters with token: " + token);
      }

      if (value < Long.MIN_VALUE) {
        LOGGER.debug("-- {} :: {} must at least be {} characters with token: {} --", methodName, numberType,
            Long.MIN_VALUE, token);
        throw new NumberLengthException(methodName + " :: " + numberType + " must at least be " + Long.MIN_VALUE
            + " characters with token: " + token);
      }
    }

    if (!isLong) {
      if (value > Integer.MAX_VALUE) {
        LOGGER.debug("-- {} :: {} must at most be {} characters with token: {} --", methodName, numberType,
            Integer.MAX_VALUE, token);
        throw new NumberLengthException(methodName + " :: " + numberType + " must at most be " + Integer.MAX_VALUE
            + " characters with token: " + token);
      }

      if (value < Integer.MIN_VALUE) {
        LOGGER.debug("-- {} :: {} must at least be {} characters with token: {} --", methodName, numberType,
            Integer.MIN_VALUE, token);
        throw new NumberLengthException(methodName + " :: " + numberType + " must at least be " + Integer.MIN_VALUE
            + " characters with token: " + token);
      }
    }
  }

  /**
   * Handling number objects that do not exist.
   *
   * @param numberObj  numberObj
   * @param numberId   numberId
   * @param methodText methodText
   */
  public static void errorHandlingNonExistingNumber(final Number numberObj, final Long numberId,
      final String methodText) {
    if (numberObj == null) {
      LOGGER.debug("-- {} :: Number object with id = {} does not exist --", methodText, numberId);
      throw new NumberNotFoundException(Number.class, numberId);
    }
  }

  public static void errorHandlingExpiredToken(final ZonedDateTime refreshToken, final String fieldName,
      final String methodText) {
    LOGGER.debug("-- {} :: {} expired at {} --", methodText, fieldName, refreshToken);
    throw new TokenExpiredException(methodText + " :: " + fieldName + " expired at: " + refreshToken);
  }
}
