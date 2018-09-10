package square.app.exceptions;

public class ObjectNotFoundException extends RuntimeException {

  private final Class<?> entityNotFound;

  public ObjectNotFoundException(final Class<?> entityNotFound, final Object id) {
    super(String.format("%s with id = '%s' does not exist", entityNotFound.getSimpleName(), id));
    this.entityNotFound = entityNotFound;
  }

  public Class<?> getEntityNotFound() {
    return entityNotFound;
  }
}
