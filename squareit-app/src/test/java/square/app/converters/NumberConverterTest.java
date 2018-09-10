package square.app.converters;

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
public class NumberConverterTest {

  @Test(expected = InvocationTargetException.class)
  public void numberConverter_ShouldFail_ConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    Constructor<NumberConverter> constructor = NumberConverter.class.getDeclaredConstructor();
    Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
