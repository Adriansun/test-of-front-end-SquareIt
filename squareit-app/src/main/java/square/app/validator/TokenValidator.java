package square.app.validator;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import square.app.constants.TimeConstants;
import square.app.exceptions.TimeException;

public final class TokenValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenValidator.class);

  private TokenValidator() {
    throw new IllegalStateException("TokenValidator :: Cannot be instantiated");
  }

  /**
   * Checks if token expiration date is valid.
   *
   * @param expirationDate expirationDate
   * @return isValid
   */
  public static boolean isValidExpirationToken(final ZonedDateTime expirationDate) {
    if (expirationDate == null) {
      LOGGER.debug("-- isValidExpirationToken :: User expirationDate is null --");
      throw new TimeException("isValidExpirationToken :: User expirationDate is null");
    }

    final long timeNow = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).toInstant().toEpochMilli();
    final long timeExpire = expirationDate.toInstant().toEpochMilli();

    return (timeExpire - timeNow) >= 0;
  }

  /**
   * Checks is the temporary login token time is valid.
   *
   * @param refreshToken refreshToken
   * @return isValid
   */
  public static boolean isValidLoginToken(final ZonedDateTime refreshToken) {
    if (refreshToken == null) {
      LOGGER.debug("-- isValidLoginToken :: User current time of token is null --");
      throw new TimeException("isValidLoginToken :: User current time of token is null");
    }

    final long timeNow = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).toInstant().toEpochMilli();
    final long timeExpire = refreshToken.plusHours(TimeConstants.TOKEN_VALID_FOR_2_HOURS).toInstant()
        .toEpochMilli();

    return (timeExpire - timeNow) >= 0;
  }
}
