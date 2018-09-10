package square.api.domain.models.number;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.io.IOException;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import square.api.domain.util.JsonUtil;
import square.api.domain.utils.TestObjects;

@ActiveProfiles({"dev"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CreateNumberRequestTest {

  private static CreateNumberRequest.Builder request;

  private static Validator validator;

  private static String errorMsg;

  /**
   * Setup.
   */
  @Before
  public void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    request = TestObjects.createCorrectNumberRequestBuilder();
  }

  @Test
  public void createNumberRequest_ShouldWork_CreateCompleteObjectWithCorrectValues() {
    final Set<ConstraintViolation<CreateNumberRequest>> violations = validator.validate(request.build());
    Assert.assertTrue(violations.isEmpty());
  }

  @Test
  public void createNumberRequest_ShouldWork_MarshallObject() {
    final String serializedString = JsonUtil.writeObjectAsString(request.build());
    Assertions.assertThat(serializedString).isEqualTo(request.build().toString());
  }

  @Test
  public void createNumberRequest_ShouldWork_UnMarshallObject() throws IOException {
    final CreateNumberRequest createNumberRequest = request.build();

    final CreateNumberRequest readValue = JsonUtil.getJsonMapper()
        .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
        .readValue(createNumberRequest.toString(), CreateNumberRequest.class);

    Assertions.assertThat(readValue.getNumberId()).isEqualTo(createNumberRequest.getNumberId());
    Assertions.assertThat(readValue.getNumber()).isEqualTo(createNumberRequest.getNumber());
    Assertions.assertThat(readValue.getToken()).isEqualTo(createNumberRequest.getToken());
  }

  @Test
  public void createNumberRequest_ShouldWork_NumberIdNull() {
    request.withNumberId(null);
    final Set<ConstraintViolation<CreateNumberRequest>> violations = validator.validate(request.build());

    Assert.assertTrue(violations.isEmpty());
  }

  @Test
  public void createNumberRequest_ShouldWork_NumberNull() {
    request.withNumber(null);
    final Set<ConstraintViolation<CreateNumberRequest>> violations = validator.validate(request.build());

    Assert.assertTrue(violations.isEmpty());
  }

  @Test
  public void createNumberRequest_ShouldFail_TokenTooLong() {
    final String tokenTooLong = "tokentokentokentokentokentokentokentoken";

    request.withToken(tokenTooLong);
    final Set<ConstraintViolation<CreateNumberRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Token must be 36 characters");
  }

  @Test
  public void createNumberRequest_ShouldFail_TokenTooShort() {
    final String tokenTooLong = "token";

    request.withToken(tokenTooLong);
    final Set<ConstraintViolation<CreateNumberRequest>> violations = validator.validate(request.build());

    Assert.assertFalse(violations.isEmpty());
    errorMsg = getErrorMessage(violations);
    Assertions.assertThat(errorMsg).isEqualTo("Token must be 36 characters");
  }

  private String getErrorMessage(final Set<ConstraintViolation<CreateNumberRequest>> violations) {
    final String[] errorMsg = {null};
    violations.iterator().forEachRemaining(msg -> errorMsg[0] = msg.getMessage());

    return errorMsg[0];
  }
}
