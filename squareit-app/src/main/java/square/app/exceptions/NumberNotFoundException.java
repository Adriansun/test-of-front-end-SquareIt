package square.app.exceptions;

public class NumberNotFoundException extends ObjectNotFoundException {

  public NumberNotFoundException(final Class clazz, final Object id) {
    super(clazz, id);
  }
}
