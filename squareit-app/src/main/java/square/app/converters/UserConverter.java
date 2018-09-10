package square.app.converters;

import square.api.domain.models.user.UserDto;

import square.app.domain.jpa.Users;

public final class UserConverter {

  private UserConverter() {
    throw new IllegalStateException("UserConverter class :: Cannot be instantiated");
  }

  /**
   * User to UserDto converter for the response.
   *
   * @param user user
   * @return UserDto
   */
  public static UserDto toUserDto(final Users user, final String token) {
    return UserDto.newBuilder()
        .withUserName(user.getUserName())
        .withFirstName(user.getFirstName())
        .withLastName(user.getLastName())
        .withEmail(user.getEmail())
        .withRole(user.getRole())
        .withEnabled(user.getEnabled())
        .withToken(token)
        .build();
  }
}
