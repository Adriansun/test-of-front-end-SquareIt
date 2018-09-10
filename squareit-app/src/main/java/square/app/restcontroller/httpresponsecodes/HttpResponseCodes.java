package square.app.restcontroller.httpresponsecodes;

public class HttpResponseCodes {

  private HttpResponseCodes() {
    throw new IllegalStateException("HttpResponseCodes :: Cannot be instantiated");
  }

  public static final int OK = 200;
  public static final int CREATED = 201;
  public static final int BAD_REQUEST = 400;
  public static final int NOT_FOUND = 404;
  public static final int NOT_ACCEPTABLE = 406;
  public static final int UNSUPPORTED_MEDIA_TYPE = 415;
  public static final int INTERNAL_SERVER_ERROR = 500;

}
