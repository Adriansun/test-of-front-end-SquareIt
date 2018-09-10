package square.app.exceptions;

public class EmailLengthException extends RuntimeException {

  public EmailLengthException(final String message) {
    super(message);
  }
}