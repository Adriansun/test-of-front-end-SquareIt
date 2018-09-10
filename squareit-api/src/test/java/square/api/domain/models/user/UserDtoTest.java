package square.api.domain.models.user;

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
public class UserDtoTest {

  private static UserDto.Builder request;

  private static Validator validator;

  /**
   * Setup.
   */
  @Before
  public void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    request = TestObjects.createCorrectUserDtoBuilder();
  }

  @Test
  public void userDto_ShouldWork_CreateCompleteObjectWithCorrectValues() {
    final Set<ConstraintViolation<UserDto>> violations = validator.validate(request.build());
    Assert.assertTrue(violations.isEmpty());
  }

  @Test
  public void userDto_ShouldWork_MarshallObject() {
    final String serializedString = JsonUtil.writeObjectAsString(request.build());
    Assertions.assertThat(serializedString).isEqualTo(request.build().toString());
  }

  @Test
  public void userDto_ShouldWork_UnMarshallObject() throws IOException {
    final UserDto userDto = request.build();

    final UserDto readValue = JsonUtil.getJsonMapper()
        .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
        .readValue(userDto.toString(), UserDto.class);

    Assertions.assertThat(readValue.getUserName()).isEqualTo(userDto.getUserName());
    Assertions.assertThat(readValue.getFirstName()).isEqualTo(userDto.getFirstName());
    Assertions.assertThat(readValue.getLastName()).isEqualTo(userDto.getLastName());
    Assertions.assertThat(readValue.getEmail()).isEqualTo(userDto.getEmail());
    Assertions.assertThat(readValue.getEnabled()).isEqualTo(userDto.getEnabled());
    Assertions.assertThat(readValue.getRole()).isEqualTo(userDto.getRole());
    Assertions.assertThat(readValue.getToken()).isEqualTo(userDto.getToken());
  }
}
