package square.app;

import java.time.ZoneId;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import square.app.constants.TimeConstants;

@SpringBootApplication
public class SquareItApplication extends SpringBootServletInitializer {

  @PostConstruct
  void started() {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC)));
  }

  /**
   * Application starter, creates a JAR file.
   *
   * @param args args
   */
  public static void main(String[] args) {
    SpringApplication.run(SquareItApplication.class, args);
  }

  /**
   * Creates a WAR file of the project.
   *
   * @param builder builder
   * @return the war
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(SquareItApplication.class);
  }
}
