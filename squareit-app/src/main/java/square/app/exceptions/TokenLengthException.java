package square.app.exceptions;

public class TokenLengthException extends RuntimeException {

  public TokenLengthException(final String message) {
    super(message);
  }
}