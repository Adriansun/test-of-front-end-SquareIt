package square.api.domain.utils;

import square.api.domain.constants.RoleTypes;
import square.api.domain.models.GenericMessageResponse;
import square.api.domain.models.number.CreateNumberRequest;
import square.api.domain.models.number.NumberDto;
import square.api.domain.models.user.CreateUserRequest;
import square.api.domain.models.user.UserDto;

public class TestObjects {

  private static final String USER_NAME = "Batman_ 1";
  private static final String USER_EMAIL = "hans@larsson.com";
  private static final String USER_FIRST_NAME = "Hans";
  private static final String USER_LAST_NAME = "Larsson";
  private static final String USER_PASSWORD = "abcdeF!2";
  private static final String USER_CONFIRM_PASSWORD = "abcdeF!2";
  private static final String TOKEN = "token";

  private static final Long NUMBER_ID = 2L;
  private static final Long NUMBER = 5L;
  private static final Long NUMBER_SQUARED = 25L;
  private static final String LONG_TOKEN = "tokentokentokentokentokentokentoken1";

  /**
   * Create correct UserRequest builder.
   *
   * @return return
   */
  public static CreateUserRequest.Builder createCorrectUserRequestBuilder() {
    return CreateUserRequest.newBuilder()
        .withUserName(USER_NAME)
        .withEmail(USER_EMAIL)
        .withFirstName(USER_FIRST_NAME)
        .withLastName(USER_LAST_NAME)
        .withPassword(USER_PASSWORD)
        .withConfirmPassword(USER_CONFIRM_PASSWORD)
        .withRole(RoleTypes.USER_ROLE);
  }

  /**
   * Create correct UserDto request builder.
   *
   * @return return
   */
  public static UserDto.Builder createCorrectUserDtoBuilder() {
    return UserDto.newBuilder()
        .withUserName(USER_NAME)
        .withEmail(USER_EMAIL)
        .withFirstName(USER_FIRST_NAME)
        .withLastName(USER_LAST_NAME)
        .withRole(RoleTypes.USER_ROLE)
        .withEnabled(false)
        .withToken(TOKEN);
  }

  public static GenericMessageResponse createCorrectGenericMessageResponse() {
    return new GenericMessageResponse().withMessage("hello", TOKEN);
  }

  /**
   * Create correct NumberRequest builder.
   *
   * @return return
   */
  public static CreateNumberRequest.Builder createCorrectNumberRequestBuilder() {
    return CreateNumberRequest.newBuilder()
        .withNumberId(NUMBER_ID)
        .withNumber(NUMBER)
        .withToken(LONG_TOKEN);
  }

  /**
   * Create correct NumberDto builder.
   *
   * @return return
   */
  public static NumberDto.Builder createCorrectNumberDtoBuilder() {
    return NumberDto.newBuilder()
        .withNumberId(NUMBER_ID)
        .withNumber(NUMBER)
        .withToken(LONG_TOKEN)
        .withNumberSquared(NUMBER_SQUARED);
  }
}
