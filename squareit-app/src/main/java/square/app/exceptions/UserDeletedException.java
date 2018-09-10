package square.app.exceptions;

public class UserDeletedException extends RuntimeException {

  public UserDeletedException(final String message) {
    super(message);
  }
}