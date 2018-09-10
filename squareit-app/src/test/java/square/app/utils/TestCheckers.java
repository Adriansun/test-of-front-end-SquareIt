package square.app.utils;

import javax.annotation.PostConstruct;

import org.assertj.core.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.models.user.CreateUserRequest;
import square.api.domain.models.user.UserDto;

import square.app.domain.jpa.Users;

public class TestCheckers {

  private PasswordEncoder passwordEncoder;

  private PasswordEncoder passwordEncoder1;

  @Autowired
  public TestCheckers(final PasswordEncoder passwordEncoder1) {
    this.passwordEncoder = passwordEncoder1;
  }

  @PostConstruct
  private void initStaticPasswordEncoder() {
    passwordEncoder = this.passwordEncoder1;
  }

  public void checkHashedPassword(final CreateUserRequest request, final Users user) {
    Assertions.assertThat(passwordEncoder.matches(request.getPassword(), user.getPassword()))
        .isEqualTo(true);
  }

  /**
   * General error checker.
   *
   * @param response            response
   * @param code                code
   * @param expectedDescription expectedDescription
   */
  public static void checkErrorResponse(final ErrorInfo response, final ErrorCode code,
      final String expectedDescription) {
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getErrorCode()).isEqualTo(code);
    Assertions.assertThat(response.getErrorDescription()).isEqualTo(expectedDescription);
  }

  /**
   * Checks UserDto vs persistent User.
   *
   * @param userDto userDto
   * @param user    user
   */
  public static void checkCorrectUserDtoVsUser(final UserDto userDto, final Users user, final boolean enabled) {
    Assertions.assertThat(user.getVerificationToken().getToken()).isEqualTo(userDto.getToken());
    Assertions.assertThat(user.getFirstName()).isEqualTo(userDto.getFirstName());
    Assertions.assertThat(user.getLastName()).isEqualTo(userDto.getLastName());
    Assertions.assertThat(user.getUserName()).isEqualTo(userDto.getUserName());
    Assertions.assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    Assertions.assertThat(user.getRole()).isEqualTo(userDto.getRole());
    Assertions.assertThat(user.getEnabled()).isEqualTo(enabled);

    Assertions.assertThat(user.getVerificationToken().getToken()).isEqualTo(userDto.getToken());
    Assertions.assertThat(user.getVerificationToken().getId().toString()).isNotEmpty();
    Assertions.assertThat(user.getVerificationToken().getExpiryDate().toString()).isNotEmpty();
    Assertions.assertThat(user.getVerificationToken().getRefreshToken().toString()).isNotEmpty();
    Assertions.assertThat(user.getVerificationToken().getUser()).isNotNull();
  }

  /**
   * Checks UserRequest vs UserDto response vs persistent Users.
   *
   * @param req     request
   * @param userDto response
   * @param user    user
   */
  public static void checkCorrectCreateUserRequestVsUserDtoVsPersistentUser(final CreateUserRequest req,
      final UserDto userDto, final Users user, final boolean enabled) {
    Assertions.assertThat(user.getFirstName()).isEqualTo(req.getFirstName());
    Assertions.assertThat(user.getLastName()).isEqualTo(req.getLastName());
    Assertions.assertThat(user.getUserName()).isEqualTo(req.getUserName());
    Assertions.assertThat(user.getEmail()).isEqualTo(req.getEmail());
    Assertions.assertThat(user.getRole()).isEqualTo(req.getRole());

    Assertions.assertThat(user.getVerificationToken().getToken()).isEqualTo(userDto.getToken());
    Assertions.assertThat(user.getFirstName()).isEqualTo(userDto.getFirstName());
    Assertions.assertThat(user.getLastName()).isEqualTo(userDto.getLastName());
    Assertions.assertThat(user.getUserName()).isEqualTo(userDto.getUserName());
    Assertions.assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    Assertions.assertThat(user.getRole()).isEqualTo(userDto.getRole());
    Assertions.assertThat(user.getEnabled()).isEqualTo(enabled);

    Assertions.assertThat(req.getFirstName()).isEqualTo(userDto.getFirstName());
    Assertions.assertThat(req.getLastName()).isEqualTo(userDto.getLastName());
    Assertions.assertThat(req.getUserName()).isEqualTo(userDto.getUserName());
    Assertions.assertThat(req.getEmail()).isEqualTo(userDto.getEmail());
    Assertions.assertThat(req.getRole()).isEqualTo(userDto.getRole());
    Assertions.assertThat(req.getEnabled()).isEqualTo(enabled);

    Assertions.assertThat(user.getVerificationToken().getToken()).isEqualTo(userDto.getToken());
    Assertions.assertThat(user.getVerificationToken().getId().toString()).isNotEmpty();
    Assertions.assertThat(user.getVerificationToken().getExpiryDate().toString()).isNotEmpty();
    Assertions.assertThat(user.getVerificationToken().getRefreshToken().toString()).isNotEmpty();
    Assertions.assertThat(user.getVerificationToken().getUser()).isNotNull();
  }
}
