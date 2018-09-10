package square.api.domain.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PasswordsValidator.class})
public @interface ValidatePasswords {

  /**
   * Validator of password and confirm password.
   *
   * @return return
   */
  String message();

  /**
   * Groups.
   *
   * @return return
   */
  Class<?>[] groups() default {};

  /**
   * Payload.
   *
   * @return return
   */
  Class<? extends Payload>[] payload() default {};
}
