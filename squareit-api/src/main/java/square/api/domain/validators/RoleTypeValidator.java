package square.api.domain.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import square.api.domain.constants.RoleTypes;

public class RoleTypeValidator implements ConstraintValidator<ValidateRoleType, RoleTypes> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoleTypeValidator.class);

  private boolean validateRoleType(final RoleTypes roleType, final ConstraintValidatorContext context) {
    LOGGER.info(">> RoleTypeValidator >>");

    if (roleType == null) {
      LOGGER.debug("-- RoleTypeValidator :: Role type may not be null --");
      context.buildConstraintViolationWithTemplate("Role type may not be null")
          .addConstraintViolation();
      return false;
    }

    for (RoleTypes type : RoleTypes.values()) {
      if (type == roleType) {
        return true;
      }
    }

    LOGGER.debug("-- RoleTypeValidator :: Not a valid roleType --");
    context.buildConstraintViolationWithTemplate("Not a valid roleType")
        .addConstraintViolation();

    LOGGER.info("<< RoleTypeValidator <<");
    return false;
  }

  @Override
  public boolean isValid(final RoleTypes roleType, final ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return validateRoleType(roleType, context);
  }

  @Override
  public void initialize(final ValidateRoleType constraintAnnotation) {
    // Initializes the validator by itself
  }
}
