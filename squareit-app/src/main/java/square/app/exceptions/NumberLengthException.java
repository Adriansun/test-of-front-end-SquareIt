package square.app.exceptions;

public class NumberLengthException extends RuntimeException {

  public NumberLengthException(final String message) {
    super(message);
  }
}