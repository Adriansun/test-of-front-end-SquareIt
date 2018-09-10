package square.app.exceptions;

public class PasswordMismatchException extends RuntimeException {

  public PasswordMismatchException(final String message) {
    super(message);
  }
}