package square.api.domain.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import square.api.domain.constants.Constraint;

public class EmailValidator implements ConstraintValidator<ValidateEmail, String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailValidator.class);

  private static final String EMAIL_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
      + "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f"
      + "])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4]["
      + "0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\"
      + "x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";

  private Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

  private boolean validateEmail(final String email, final ConstraintValidatorContext context) {
    LOGGER.info(">> EmailValidator >>");

    if (email == null || email.isEmpty()) {
      LOGGER.debug("-- UsernameValidator :: Email may not be null or empty --");
      context.buildConstraintViolationWithTemplate("Email may not be null or empty")
          .addConstraintViolation();
      return false;
    }

    if (email.length() > Constraint.MAX_EMAIL) {
      LOGGER.debug("-- UsernameValidator :: Email may not be more than {} characters --", Constraint.MAX_EMAIL);
      context.buildConstraintViolationWithTemplate("Email may not be more than "
          + Constraint.MAX_EMAIL + " characters")
          .addConstraintViolation();
      return false;
    }

    if (email.length() < Constraint.MIN_EMAIL) {
      LOGGER.debug("-- UsernameValidator :: Email may not be less than {} characters --", Constraint.MIN_EMAIL);
      context.buildConstraintViolationWithTemplate("Email may not be less than "
          + Constraint.MIN_EMAIL + " characters")
          .addConstraintViolation();
      return false;
    }

    if (!emailPattern.matcher(email).matches()) {
      LOGGER.debug("-- UsernameValidator :: Email does not follow pattern restrictions --");
      context.buildConstraintViolationWithTemplate("Email does not follow pattern restrictions")
          .addConstraintViolation();
      return false;
    }

    LOGGER.info("<< EmailValidator <<");
    return true;
  }

  @Override
  public boolean isValid(final String email, final ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return validateEmail(email, context);
  }

  @Override
  public void initialize(final ValidateEmail constraintAnnotation) {
    // Initializes the validator by itself
  }
}
