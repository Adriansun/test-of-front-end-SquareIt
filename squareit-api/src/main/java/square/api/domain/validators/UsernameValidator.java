package square.api.domain.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import square.api.domain.constants.Constraint;

public class UsernameValidator implements ConstraintValidator<ValidateUsername, String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UsernameValidator.class);

  private static final String USERNAME_PATTERN = "^[A-Za-z0-9 _]*$";

  private boolean validateUsername(final String username, final ConstraintValidatorContext context) {
    LOGGER.info(">> UsernameValidator >>");

    if (username == null || username.isEmpty()) {
      LOGGER.debug("-- UsernameValidator :: Username may not be null or empty --");
      context.buildConstraintViolationWithTemplate("Username may not be null or empty")
          .addConstraintViolation();
      return false;
    }

    if (username.length() > Constraint.MAX_USERNAME) {
      LOGGER.debug("-- UsernameValidator :: Username may not be more than {} characters --", Constraint.MAX_USERNAME);
      context.buildConstraintViolationWithTemplate("Username may not be more than "
          + Constraint.MAX_USERNAME + " characters")
          .addConstraintViolation();
      return false;
    }

    if (username.length() < Constraint.MIN_USERNAME) {
      LOGGER.debug("-- UsernameValidator :: Username may not be less than {} characters --", Constraint.MIN_USERNAME);
      context.buildConstraintViolationWithTemplate("Username may not be less than "
          + Constraint.MIN_USERNAME + " characters")
          .addConstraintViolation();
      return false;
    }

    Pattern acceptedChars = Pattern.compile(USERNAME_PATTERN);
    if (!acceptedChars.matcher(username).find()) {
      LOGGER.debug("-- UsernameValidator :: Username may only use lowercase lowercase / uppercase letters, numbers "
          + "0-9, underscores and whitespaces --");
      context.buildConstraintViolationWithTemplate(
          "Username may only use lowercase lowercase / uppercase letters, numbers 0-9, underscores "
              + "and whitespaces")
          .addConstraintViolation();
      return false;
    }

    LOGGER.info("<< UsernameValidator <<");
    return true;
  }

  @Override
  public boolean isValid(final String username, final ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return validateUsername(username, context);
  }

  @Override
  public void initialize(final ValidateUsername constraintAnnotation) {
    // Initializes the validator by itself
  }
}
