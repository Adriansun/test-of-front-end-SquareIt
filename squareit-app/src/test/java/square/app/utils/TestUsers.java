package square.app.utils;

import square.api.domain.constants.RoleTypes;
import square.api.domain.models.user.CreateUserRequest;

public class TestUsers {

  private static final String USERNAME = "Batman_ 1";
  private static final String EMAIL = "hans@larsson.com";
  private static final String FIRST_NAME = "Hans";
  private static final String LAST_NAME = "Larsson";
  private static final String PASSWORD = "abcdeF!2";
  private static final String CONFIRM_PASSWORD = "abcdeF!2";

  /**
   * Create correct user builder request.
   *
   * @return userRequest builder
   */
  public static CreateUserRequest.Builder createCorrectUserRequestBuilder() {
    return CreateUserRequest.newBuilder()
        .withUserName(USERNAME)
        .withFirstName(FIRST_NAME)
        .withLastName(LAST_NAME)
        .withEmail(EMAIL)
        .withPassword(PASSWORD)
        .withConfirmPassword(CONFIRM_PASSWORD)
        .withRole(RoleTypes.USER_ROLE);
  }

  /**
   * Create correct user builder request with enabled set to true.
   *
   * @param enabled enabled status
   * @return createUser request with enabled status
   */
  public static CreateUserRequest.Builder createCorrectUserRequestBuilder(final boolean enabled) {
    CreateUserRequest.Builder correctUserRequestBuilder = createCorrectUserRequestBuilder();

    return correctUserRequestBuilder.withEnabled(enabled);
  }
}
