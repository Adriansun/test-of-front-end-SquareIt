package square.api.domain.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import square.api.domain.constants.Constraint;
import square.api.domain.models.user.CreateUserRequest;

public class PasswordsValidator implements ConstraintValidator<ValidatePasswords, CreateUserRequest> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordsValidator.class);

  private static final String PW_AT_LEAST = "Password must have at least ";

  private static final String SPECIAL_CHARACTER_PATTERN = "[^a-z0-9 ]";
  private static final String UPPER_CASE_PATTERN = "[A-Z ]";
  private static final String LOWER_CASE_PATTERN = "[a-z ]";
  private static final String DIGIT_CASE_PATTERN = "[0-9 ]";

  private static final Pattern SPECIAL_CHARACTER = Pattern.compile(SPECIAL_CHARACTER_PATTERN, Pattern.CASE_INSENSITIVE);
  private static final Pattern UPPERCASE = Pattern.compile(UPPER_CASE_PATTERN);
  private static final Pattern LOWERCASE = Pattern.compile(LOWER_CASE_PATTERN);
  private static final Pattern DIGIT_CASE = Pattern.compile(DIGIT_CASE_PATTERN);

  private boolean validatePasswords(final CreateUserRequest userRequest, final ConstraintValidatorContext context) {
    LOGGER.info(">> PasswordsValidator >>");

    final String password = userRequest.getPassword();
    final String confirmPassword = userRequest.getConfirmPassword();

    if (password == null || password.isEmpty()) {
      LOGGER.debug("-- PasswordsValidator :: Password may not be null or empty --");
      context.buildConstraintViolationWithTemplate("Password may not be null or empty")
          .addConstraintViolation();
      return false;
    }

    if (!password.equals(confirmPassword)) {
      LOGGER.debug("-- PasswordsValidator :: Password and confirm password does not match --");
      context.buildConstraintViolationWithTemplate("Password and confirm password does not match")
          .addConstraintViolation();
      return false;
    }

    if (password.length() < Constraint.MIN_PASSWORD) {
      LOGGER.debug("-- PasswordsValidator :: Password must be at least {} characters --", Constraint.MIN_PASSWORD);
      context.buildConstraintViolationWithTemplate("Password must be at least "
          + Constraint.MIN_PASSWORD + " characters")
          .addConstraintViolation();
      return false;
    }

    if (password.length() > Constraint.MAX_PASSWORD) {
      LOGGER.debug("-- PasswordsValidator :: Password may not be more than {} characters --", Constraint.MAX_PASSWORD);
      context.buildConstraintViolationWithTemplate("Password may not be more than "
          + Constraint.MAX_PASSWORD + " characters")
          .addConstraintViolation();
      return false;
    }

    if (!SPECIAL_CHARACTER.matcher(password).find()) {
      LOGGER.debug("-- PasswordsValidator :: Password must have at least {} special character --",
          Constraint.MIN_PASSWORD_SPECIAL_CHARACTER);
      context.buildConstraintViolationWithTemplate(PW_AT_LEAST
          + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " special character")
          .addConstraintViolation();
      return false;
    }

    if (!DIGIT_CASE.matcher(password).find()) {
      LOGGER.debug("-- PasswordsValidator :: Password must have at least {} number --",
          Constraint.MIN_PASSWORD_SPECIAL_CHARACTER);
      context.buildConstraintViolationWithTemplate(PW_AT_LEAST
          + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " number")
          .addConstraintViolation();
      return false;
    }

    if (!UPPERCASE.matcher(password).find()) {
      LOGGER.debug("-- PasswordsValidator :: Password must have at least {} UPPERCASE character --",
          Constraint.MIN_PASSWORD_SPECIAL_CHARACTER);
      context.buildConstraintViolationWithTemplate(PW_AT_LEAST
          + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " UPPERCASE character")
          .addConstraintViolation();
      return false;
    }

    if (!LOWERCASE.matcher(password).find()) {
      LOGGER.debug("-- PasswordsValidator :: Password must have at least {} lowercase character --",
          Constraint.MIN_PASSWORD_SPECIAL_CHARACTER);
      context.buildConstraintViolationWithTemplate(PW_AT_LEAST
          + Constraint.MIN_PASSWORD_SPECIAL_CHARACTER + " lowercase character")
          .addConstraintViolation();
      return false;
    }

    LOGGER.info("<< PasswordsValidator <<");
    return true;
  }

  @Override
  public boolean isValid(final CreateUserRequest userRequest, final ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return validatePasswords(userRequest, context);
  }

  @Override
  public void initialize(final ValidatePasswords constraintAnnotation) {
    // Initializes the validator by itself
  }
}
