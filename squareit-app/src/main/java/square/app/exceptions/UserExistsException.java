package square.app.exceptions;

public class UserExistsException extends RuntimeException {

  public UserExistsException(final String message) {
    super(message);
  }
}
