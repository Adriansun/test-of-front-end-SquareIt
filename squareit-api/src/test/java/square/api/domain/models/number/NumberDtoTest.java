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
public class NumberDtoTest {

  private static NumberDto.Builder request;

  private static Validator validator;

  /**
   * Setup.
   */
  @Before
  public void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    request = TestObjects.createCorrectNumberDtoBuilder();
  }

  @Test
  public void numberDto_ShouldWork_CreateCompleteObjectWithCorrectValues() {
    final Set<ConstraintViolation<NumberDto>> violations = validator.validate(request.build());
    Assert.assertTrue(violations.isEmpty());
  }

  @Test
  public void numberDto_ShouldWork_MarshallObject() {
    final String serializedString = JsonUtil.writeObjectAsString(request.build());
    Assertions.assertThat(serializedString).isEqualTo(request.build().toString());
  }

  @Test
  public void numberDto_ShouldWork_UnMarshallObject() throws IOException {
    final NumberDto numberDto = request.build();

    final NumberDto readValue = JsonUtil.getJsonMapper()
        .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
        .readValue(numberDto.toString(), NumberDto.class);

    Assertions.assertThat(readValue.getNumberId()).isEqualTo(numberDto.getNumberId());
    Assertions.assertThat(readValue.getNumber()).isEqualTo(numberDto.getNumber());
    Assertions.assertThat(readValue.getNumberSquared()).isEqualTo(numberDto.getNumberSquared());
    Assertions.assertThat(readValue.getToken()).isEqualTo(numberDto.getToken());
  }
}
