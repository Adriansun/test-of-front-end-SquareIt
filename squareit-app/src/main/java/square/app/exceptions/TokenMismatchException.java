package square.app.exceptions;

public class TokenMismatchException extends RuntimeException {

  public TokenMismatchException(final String message) {
    super(message);
  }
}
