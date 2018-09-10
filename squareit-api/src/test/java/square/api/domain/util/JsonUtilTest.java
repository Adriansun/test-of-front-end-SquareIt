package square.api.domain.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles({"dev"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JsonUtilTest {

  @Test(expected = InvocationTargetException.class)
  public void jsonUtil_ShouldFail_ConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    Constructor<JsonUtil> constructor = JsonUtil.class.getDeclaredConstructor();
    Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
