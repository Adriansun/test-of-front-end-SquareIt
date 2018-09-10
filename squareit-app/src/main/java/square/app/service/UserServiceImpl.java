package square.app.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import square.api.domain.models.user.CreateUserRequest;

import square.app.constants.TimeConstants;
import square.app.constants.TokenRequestType;
import square.app.constants.UserRequestType;
import square.app.domain.dao.UserRepository;
import square.app.domain.jpa.Users;
import square.app.domain.jpa.VerificationToken;
import square.app.errorhandling.ErrorHandling;
import square.app.exceptions.PasswordMismatchException;
import square.app.exceptions.TokenMismatchException;
import square.app.validator.TokenValidator;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class UserServiceImpl implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private static final String CHECK_USER_EXISTENCE_EXIT = "<< UserServiceImpl :: checkUserExistence <<";
  private static final String CHECK_USER_EXISTENCE = "UserServiceImpl :: checkUserExistence";
  private static final String UPDATE_USER_METHOD = "UserServiceImpl :: updateUser";
  private static final String USERNAME = "username";
  private static final String EMAIL = "email";

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  /**
   * Autowiring.
   *
   * @param userRepository  userRepository
   * @param passwordEncoder passwordEncoder
   */
  @Autowired
  public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public String loginUser(final String emailOrUsername, final String password) {
    LOGGER.info(">> UserServiceImpl :: loginUser >>");

    final Users user = checkUserExistence(emailOrUsername, emailOrUsername, null, null,
        UserRequestType.LOGIN_USER);

    assert user != null;
    ErrorHandling.errorHandlingDeactivatedUser(user, emailOrUsername, EMAIL, "UserServiceImpl :: loginUser");
    ErrorHandling.errorHandlingDeletedUser(user, user.getEmail(), UPDATE_USER_METHOD);

    boolean passwordsMatch = passwordEncoder.matches(password, user.getPassword());
    if (!passwordsMatch) {
      LOGGER.debug("-- UserServiceImpl :: loginUser :: User request password does not match user password with "
          + "persistent user email: {} --", user.getEmail());
      throw new PasswordMismatchException("UserServiceImpl :: loginUser :: User request password does not match user "
          + "password with persistent user email: " + user.getEmail());
    }

    boolean validToken = TokenValidator.isValidLoginToken(user.getVerificationToken().getRefreshToken());
    if (!validToken) {
      return generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();
    }

    LOGGER.info("<< UserServiceImpl :: loginUser <<");
    return user.getVerificationToken().getToken();
  }

  @Override
  public Users upsertUser(final CreateUserRequest request, final String currentEmail, final String token,
      final UserRequestType updateType) {
    LOGGER.info(">> UserServiceImpl :: upsertUser >>");
    LOGGER.info("User request: {}", request);

    final Users user;

    if (updateType.equals(UserRequestType.CREATE_USER)) {
      user = createUser(request);
    } else {
      user = updateUser(request, currentEmail, token);
    }

    LOGGER.info("User: {}", user);
    LOGGER.info("<< UserServiceImpl :: upsertUser <<");
    return user;
  }

  @Override
  public Users createUser(final CreateUserRequest request) {
    LOGGER.info(">> UserServiceImpl :: createUser >>");

    checkUserExistence(request.getEmail(), request.getUserName(), null, null,
        UserRequestType.CREATE_USER);

    Users newUser = new Users();
    newUser.setUserName(request.getUserName());
    newUser.setEmail(request.getEmail());
    newUser.setFirstName(request.getFirstName());
    newUser.setLastName(request.getLastName());
    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    newUser.setRole(request.getRole());
    newUser.setEnabled(false);
    newUser.setCreatedDate(ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)));
    newUser.setUserDeleted(false);

    final VerificationToken verificationToken = createVerificationToken(newUser);
    newUser.setVerificationToken(verificationToken);

    LOGGER.info("<< UserServiceImpl :: createUser <<");
    return userRepository.save(newUser);
  }

  @Override
  public Users updateUser(final CreateUserRequest userToUpdate, final String currentEmail, final String token) {
    LOGGER.info(">> UserServiceImpl :: updateUser >>");

    final Users currentUser = userRepository.findByEmail(currentEmail);

    if (currentUser == null) {
      ErrorHandling.errorHandlingNonExistingUser(currentEmail, "currentEmail", UPDATE_USER_METHOD);
    }

    assert currentUser != null;
    ZonedDateTime refreshToken = currentUser.getVerificationToken().getRefreshToken();
    boolean validToken = TokenValidator.isValidLoginToken(refreshToken);

    if (!validToken) {
      ErrorHandling.errorHandlingExpiredToken(refreshToken, "current time token", UPDATE_USER_METHOD);
    }

    if (!currentUser.getVerificationToken().getToken().equals(token)) {
      LOGGER.debug("-- UserServiceImpl :: updateUser :: User token and given token is not the same");
      throw new TokenMismatchException("UserServiceImpl :: updateUser :: User token and given token is not the same, "
          + "given token: " + token + ", and user token: " + currentUser.getVerificationToken().getToken());
    }

    ErrorHandling.errorHandlingDeactivatedUser(currentUser, currentEmail, EMAIL,
        UPDATE_USER_METHOD);
    ErrorHandling.errorHandlingDeletedUser(currentUser, EMAIL, UPDATE_USER_METHOD);

    checkUserExistence(currentEmail, currentUser.getUserName(), userToUpdate.getEmail(), userToUpdate.getUserName(),
        UserRequestType.UPDATE_USER);

    currentUser.setEmail(userToUpdate.getEmail());
    currentUser.setUserName(userToUpdate.getUserName());
    currentUser.setFirstName(userToUpdate.getFirstName());
    currentUser.setLastName(userToUpdate.getLastName());
    currentUser.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));
    currentUser.setRole(userToUpdate.getRole());
    currentUser.setEnabled(userToUpdate.getEnabled());
    currentUser.setUpdatedDate(ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)));

    LOGGER.info("<< UserServiceImpl :: updateUser <<");
    return userRepository.save(currentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public Users getUserByToken(final String token) {
    LOGGER.info(">> UserServiceImpl :: getUser >>");

    final Users user = userRepository.findUserByToken(token);

    LOGGER.info("<< UserServiceImpl :: getUser <<");
    return user;
  }

  @Override
  public VerificationToken createVerificationToken(final Users user) {
    LOGGER.info(">> UserServiceImpl :: createVerificationToken >>");

    final VerificationToken newUserToken = new VerificationToken(user, createUniqueUuid());

    LOGGER.info("<< UserServiceImpl :: createVerificationToken <<");
    return newUserToken;
  }

  public Users generateToken(final Users user, final TokenRequestType tokenRequestType) {
    LOGGER.info(">> UserServiceImpl :: generateToken >>");

    if (tokenRequestType.equals(TokenRequestType.CREATE_TOKEN)) {
      user.getVerificationToken().updateToken(createUniqueUuid());
    }

    if (tokenRequestType.equals(TokenRequestType.UPDATE_TOKEN)) {
      user.getVerificationToken().updateRefreshToken(createUniqueUuid());
    }

    LOGGER.info("<< UserServiceImpl :: generateToken <<");
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Users getUserByEmail(final String email) {
    LOGGER.info(">> UserServiceImpl :: getUserByEmail >>");

    final Users user = userRepository.findByEmail(email);

    if (user == null) {
      ErrorHandling.errorHandlingNonExistingUser(email, EMAIL,
          "UserServiceImpl :: getUserByEmail");
    }

    LOGGER.info("<< UserServiceImpl :: getUserByEmail <<");
    return user;
  }

  @Override
  public void enableConfirmedUser(final Users confirmedUser) {
    LOGGER.info(">> UserServiceImpl :: enableConfirmedUser >>");

    confirmedUser.setEnabled(true);

    LOGGER.info("<< UserServiceImpl :: enableConfirmedUser <<");
    userRepository.save(confirmedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public long countAllUsers() {
    LOGGER.info(">><< UserServiceImpl :: countAllActiveUsers >><<");
    return userRepository.countAllActiveUsers();
  }

  @Override
  public void deleteUser(final Users user) {
    LOGGER.info(">> UserServiceImpl :: deleteUser >>");

    user.setUserDeleted(true);
    userRepository.save(user);

    LOGGER.info("<< UserServiceImpl :: deleteUser <<");
  }

  private String createUniqueUuid() {
    LOGGER.info(">><< UserServiceImpl :: createUniqueUuid >><<");
    return UUID.randomUUID().toString();
  }

  private Users checkUserExistence(final String email, final String username, final String newEmail,
      final String newUsername, final Enum userTypes) {
    LOGGER.info(">> UserServiceImpl :: checkUserExistence >>");

    final Users userEmail = userRepository.findByEmail(email);
    final Users userUserName = userRepository.findByUserName(username);

    switch ((UserRequestType) userTypes) {
      case CREATE_USER:
        checkCaseCreateUser(userEmail, userUserName, email, username);
        break;
      case UPDATE_USER:
        checkCaseUpdateUser(email, username, newEmail, newUsername);
        break;
      case LOGIN_USER:
        return checkCaseLoginUser(userEmail, userUserName, email, username);
      default:
        break;
    }

    LOGGER.info(CHECK_USER_EXISTENCE_EXIT);
    return null;
  }

  private void checkCaseCreateUser(final Users userEmail, final Users userUserName, final String email,
      final String username) {
    if (userEmail != null) {
      ErrorHandling.errorHandlingExistingUser(email, EMAIL, "User already exist with email: ");
    }

    if (userUserName != null) {
      ErrorHandling.errorHandlingExistingUser(username, USERNAME,
          "User already exist with username: ");
    }
  }

  private void checkCaseUpdateUser(final String email, final String username, final String newEmail,
      final String newUsername) {
    if (!email.equals(newEmail)) {
      boolean newEmailExists = userRepository.existsByEmail(newEmail);
      if (newEmailExists) {
        ErrorHandling.errorHandlingExistingUser(newEmail, EMAIL,
            "User already exist with email: ");
      }
    }

    if (!username.equals(newUsername)) {
      boolean newUsernameExists = userRepository.existsByUserName(newUsername);
      if (newUsernameExists) {
        ErrorHandling.errorHandlingExistingUser(username, USERNAME,
            "User already exist with username: ");
      }
    }
  }

  private Users checkCaseLoginUser(final Users userEmail, final Users userUserName, final String email,
      final String username) {
    if (userEmail == null && userUserName == null) {
      ErrorHandling.errorHandlingNonExistingUser(email, "email/username",
          CHECK_USER_EXISTENCE);
    }

    if (userEmail != null && userEmail.getEmail().equals(email)) {
      LOGGER.info(CHECK_USER_EXISTENCE_EXIT);
      return userEmail;
    }

    if (userUserName != null && userUserName.getUserName().equals(username)) {
      LOGGER.info(CHECK_USER_EXISTENCE_EXIT);
      return userUserName;
    }

    return null;
  }
}
