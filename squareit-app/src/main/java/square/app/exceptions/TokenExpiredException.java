package square.app.exceptions;

public class TokenExpiredException extends RuntimeException {

  public TokenExpiredException(final String message) {
    super(message);
  }
}