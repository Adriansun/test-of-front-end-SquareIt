package square.app.exceptions;

public class UserNotActivatedException extends RuntimeException {

  public UserNotActivatedException(final String message) {
    super(message);
  }
}