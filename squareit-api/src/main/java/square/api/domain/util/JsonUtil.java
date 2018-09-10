package square.api.domain.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

  private static final ObjectMapper MAPPER;

  private JsonUtil() {
    throw new IllegalStateException("JsonUtil :: Cannot be instantiated");
  }

  static {
    MAPPER = new ObjectMapper();
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    MAPPER.registerModule(new JavaTimeModule());
  }

  public static ObjectMapper getJsonMapper() {
    return MAPPER;
  }

  /**
   * Write object as a string.
   *
   * @param object dto
   * @return json
   */
  public static String writeObjectAsString(final Object object) {
    try {
      return getJsonMapper().writeValueAsString(object);
    } catch (JsonProcessingException ex) {
      LOGGER.error("-- JsonUtil :: Unable to write object to string --", ex);
      return ex.getMessage();
    }
  }
}
