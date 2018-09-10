package square.app.service;

import square.api.domain.models.user.CreateUserRequest;

import square.app.constants.TokenRequestType;
import square.app.constants.UserRequestType;
import square.app.domain.jpa.Users;
import square.app.domain.jpa.VerificationToken;

public interface UserService {

  Users upsertUser(final CreateUserRequest request, final String currentEmail, final String token,
      final UserRequestType updateType);

  Users createUser(final CreateUserRequest request);

  VerificationToken createVerificationToken(final Users user);

  Users getUserByToken(final String token);

  Users generateToken(final Users user, final TokenRequestType tokenRequestType);

  void enableConfirmedUser(final Users confirmedUser);

  Users updateUser(final CreateUserRequest userUpdateDto, final String currentEmail, final String token);

  Users getUserByEmail(final String email);

  long countAllUsers();

  void deleteUser(final Users user);

  String loginUser(final String emailOrUserName, final String password);

}
