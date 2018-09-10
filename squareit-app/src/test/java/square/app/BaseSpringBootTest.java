package square.app;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import square.api.domain.headersdefinition.HeadersDefinition;

import square.app.domain.dao.NumberRepository;
import square.app.domain.dao.TokenRepository;
import square.app.domain.dao.UserRepository;

@ActiveProfiles({"dev"})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSpringBootTest {

  @Autowired
  protected NumberRepository numberRepository;

  @Autowired
  protected TokenRepository tokenRepository;

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  private TestRestTemplate restTemplate;

  private String breadCrumbId;

  /**
   * Before each test.
   */
  @Before
  public final void createTestUtils() {
    // Set http header 'breadcrumbId' for rest requests.
    breadCrumbId = UUID.randomUUID().toString();
    restTemplate.getRestTemplate().setInterceptors(
        Collections.singletonList((request, body, execution) -> {
          request.getHeaders()
              .add(HeadersDefinition.BREAD_CRUMB_ID, breadCrumbId);
          return execution.execute(request, body);
        }));
  }

  /**
   * After each test.
   */
  @After
  public final void clearDatabase() {
    tokenRepository.deleteAll();
    numberRepository.deleteAll();
    userRepository.deleteAll();
  }

  protected <T> T httpGet(String url, HttpStatus expectedStatus, Class<T> responseType, String... parameters) {
    if (HttpStatus.OK.equals(expectedStatus)) {
      final ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, responseType, (Object[]) parameters);
      Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
          .isEqualTo(expectedStatus);
      return responseEntity.getBody();
    } else {
      final ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, (Object[]) parameters);
      Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
          .isEqualTo(expectedStatus);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  protected <T> List<T> httpGetList(String url, HttpStatus expectedStatus, Class<T> responseType,
      String... parameters) {
    Class<T[]> arrayResponseClass = (Class<T[]>) Array.newInstance(responseType, 0).getClass();
    T[] response = httpGet(url, expectedStatus, arrayResponseClass, parameters);

    if (response == null) {
      return null;
    }

    return Arrays.asList(response);
  }

  protected <T> T httpGetError(String url, HttpStatus expectedStatus, Class<T> responseType, String... parameters) {
    final ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, responseType, (Object[]) parameters);
    Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
        .isEqualTo(expectedStatus);

    return responseEntity.getBody();
  }

  protected <T> T httpPut(String url, HttpStatus expectedStatus, Object request, Class<T> responseType,
      String... parameters) {
    if (HttpStatus.OK.equals(expectedStatus)) {
      final ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,
          new HttpEntity<>(request), responseType, (Object[]) parameters);
      Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
          .isEqualTo(expectedStatus);
      return responseEntity.getBody();
    } else {
      final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,
          new HttpEntity<>(request), String.class, (Object[]) parameters);
      Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
          .isEqualTo(expectedStatus);

      return null;
    }
  }

  protected <T> T httpPutError(String url, HttpStatus expectedStatus, Object request, Class<T> responseType,
      String... parameters) {
    final ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,
        new HttpEntity<>(request), responseType, (Object[]) parameters);
    Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
        .isEqualTo(expectedStatus);

    return responseEntity.getBody();
  }

  protected <T> T httpPost(String url, HttpStatus expectedStatus, Object request, Class<T> responseType,
      String... parameters) {
    if (HttpStatus.OK.equals(expectedStatus)) {
      final ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, request, responseType,
          (Object[]) parameters);
      Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
          .isEqualTo(expectedStatus);

      return responseEntity.getBody();
    } else {
      final ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class,
          (Object[]) parameters);
      Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
          .isEqualTo(expectedStatus);

      return null;
    }
  }

  protected <T> T httpPostError(String url, HttpStatus expectedStatus, Object request, Class<T> responseType,
      String... parameters) {
    final ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, request, responseType,
        (Object[]) parameters);
    Assertions.assertThat(responseEntity.getStatusCode()).as("Invalid HTTP status")
        .isEqualTo(expectedStatus);

    return responseEntity.getBody();
  }
}
