package square.api.domain.constants;

public class Constraint {

  private Constraint() {
    throw new IllegalStateException("Constraint class :: Cannot be instantiated");
  }

  public static final int MAX_FIRSTNAME = 30;
  public static final int MAX_LASTNAME = 30;
  public static final int MAX_USERNAME = 30;
  public static final int MAX_EMAIL = 50;
  public static final int MAX_PASSWORD = 30;
  public static final int MAX_ROLETYPE = 17;

  public static final int MAX_PASSWORD_DB = 60;
  public static final int MAX_MIN_TOKEN = 36;

  public static final int MIN_USERNAME = 2;
  public static final int MIN_EMAIL = 6;
  public static final int MIN_PASSWORD = 8;
  public static final int MIN_PASSWORD_SPECIAL_CHARACTER = 1;

}
