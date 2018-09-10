package square.app.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import square.app.constants.TimeConstants;
import square.app.exceptions.TimeException;

@ActiveProfiles({"dev"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TokenValidatorTest {

  private static ZonedDateTime aTime;

  @Test(expected = InvocationTargetException.class)
  public void tokenValidator_ShouldFail_ConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    Constructor<TokenValidator> constructor = TokenValidator.class.getDeclaredConstructor();
    Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void isValidExpirationToken_ShouldWork_GetValidToken() {
    aTime = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).plusHours(2);
    final boolean isValid = TokenValidator.isValidExpirationToken(aTime);

    Assertions.assertThat(isValid).isEqualTo(true);
  }

  @Test
  public void isValidExpirationToken_ShouldWork_GetInValidToken() {
    aTime = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).minusDays(2);
    final boolean isValid = TokenValidator.isValidExpirationToken(aTime);

    Assertions.assertThat(isValid).isEqualTo(false);
  }

  @Test
  public void isValidExpirationToken_ShouldWork_ParamIsNull() {
    try {
      TokenValidator.isValidExpirationToken(null);
    } catch (TimeException ex) {
      Assertions.assertThat(ex.getMessage()).isEqualTo("isValidExpirationToken :: User expirationDate is null");
    }
  }

  @Test
  public void isValidLoginToken_ShouldWork_GetValidToken() {
    aTime = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).plusHours(2);
    final boolean isValid = TokenValidator.isValidLoginToken(aTime);

    Assertions.assertThat(isValid).isEqualTo(true);
  }

  @Test
  public void isValidLoginToken_ShouldWork_GetInValidToken() {
    aTime = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)).minusDays(2);
    final boolean isValid = TokenValidator.isValidLoginToken(aTime);

    Assertions.assertThat(isValid).isEqualTo(false);
  }

  @Test
  public void isValidLoginToken_ShouldWork_ParamIsNull() {
    try {
      TokenValidator.isValidLoginToken(null);
    } catch (TimeException ex) {
      Assertions.assertThat(ex.getMessage()).isEqualTo("isValidLoginToken :: User current time of token is null");
    }
  }
}
