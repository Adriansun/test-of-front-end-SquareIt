package square.api.domain.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.io.IOException;

import org.assertj.core.api.Assertions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import square.api.domain.util.JsonUtil;
import square.api.domain.utils.TestObjects;

@ActiveProfiles({"dev"})
@RunWith(SpringJUnit4ClassRunner.class)
public class GenericMessageResponseTest {

  private static GenericMessageResponse genericMessageResponse;

  /**
   * Setup.
   */
  @Before
  public void setUp() {
    genericMessageResponse = TestObjects.createCorrectGenericMessageResponse();
  }

  @Test
  public void genericMessageResponse_ShouldWork_CreateCompleteObjectWithCorrectValues() {
    Assertions.assertThat(genericMessageResponse.getMessage()).isEqualTo("hello");
    Assertions.assertThat(genericMessageResponse.getToken()).isEqualTo("token");
  }

  @Test
  public void genericMessageResponse_ShouldWork_MarshallObject() {
    final String serializedString = JsonUtil.writeObjectAsString(genericMessageResponse);
    Assertions.assertThat(serializedString).isEqualTo(genericMessageResponse.toString());
  }

  @Test
  public void genericMessageResponse_ShouldWork_UnMarshallObject() throws IOException {
    final GenericMessageResponse readValue = new ObjectMapper()
        .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
        .readValue(genericMessageResponse.toString(), GenericMessageResponse.class);

    Assertions.assertThat(readValue.getMessage()).isEqualTo(genericMessageResponse.getMessage());
    Assertions.assertThat(readValue.getToken()).isEqualTo(genericMessageResponse.getToken());
  }

  @Test
  public void genericMessageResponse_ShouldWork_SetAllToNull() {
    genericMessageResponse.withMessage(null, null);

    Assertions.assertThat(genericMessageResponse.getMessage()).isEqualTo(null);
    Assertions.assertThat(genericMessageResponse.getToken()).isEqualTo(null);
  }
}
