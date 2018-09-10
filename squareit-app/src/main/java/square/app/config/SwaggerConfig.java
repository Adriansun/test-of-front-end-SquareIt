package square.app.config;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  /**
   * A bean constructor for Swagger2 api.
   *
   * @return Swagger2 api
   */
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .directModelSubstitute(ZonedDateTime.class, String.class)
        .select()
        .paths(PathSelectors.any())
        .apis(RequestHandlerSelectors.any())
        .build()
        .apiInfo(metaInfo());
  }

  private ApiInfo metaInfo() {
    return new ApiInfo(
        "SquareIt Template",
        "A template of what a Spring Boot rest service project may look like",
        "1.0",
        "Terms of service: If you gain resources out of this project then 2% of the continues profit "
            + "must go to the creator",
        new Contact("Adrian", "https://www.github.com/adriansun/squareit",
            "squareittemplate@gmail.com"),
        "Apache License Version 2.0",
        "https://www.apache.org/licenses/LICENSE-2.0",
        Collections.emptyList()
    );
  }
}
