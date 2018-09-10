package square.app.exceptions;

public class UserAlreadyActivatedException extends RuntimeException {

  public UserAlreadyActivatedException(final String message) {
    super(message);
  }
}
