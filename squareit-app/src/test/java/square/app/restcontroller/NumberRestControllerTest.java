package square.app.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.http.HttpStatus;

import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.models.GenericMessageResponse;
import square.api.domain.models.number.CreateNumberRequest;
import square.api.domain.models.number.NumberDto;
import square.api.domain.models.user.CreateUserRequest;
import square.api.domain.models.user.UserDto;

import square.app.BaseSpringBootTest;
import square.app.domain.jpa.Number;
import square.app.domain.jpa.Users;
import square.app.utils.TestCheckers;
import square.app.utils.TestUsers;

public class NumberRestControllerTest extends BaseSpringBootTest {

  private static final String NUMBER_URL = "/rest/number";

  private static final String USER_URL = "/rest/user";

  /**
   * Before each test.
   */
  @Before
  public void setUp() {
    tearDown();

    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(0);
    Assertions.assertThat(userRepository.findAll()).hasSize(0);
  }

  /**
   * After each test.
   */
  @After
  public void tearDown() {
    numberRepository.deleteAll();
    tokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void saveNumber_ShouldWork_SaveANumberForOneUser() {
    final String token = createCorrectUserAndActivateUser();
    final long number = 1L;

    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);

    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(number, token);
    final NumberDto response = callCreateNumberPutOk(request);
    Assertions.assertThat(numberRepository.findAll()).hasSize(1);

    final Long respNumberId = response.getNumberId();
    if (numberRepository.findById(respNumberId).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respNumberId).get().getNumber()).isEqualTo(number);
    }
  }

  @Test
  public void saveNumber_ShouldWork_SaveManyNumbersForOneUser() {
    final String token = createCorrectUserAndActivateUser();
    final long number1 = 10L;
    final long number2 = 20L;
    final long number3 = 30L;

    Assertions.assertThat(userRepository.findAll()).hasSize(1);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(1);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);

    final CreateNumberRequest.Builder request1 = createCorrectCreateNumberRequest(number1, token);
    final NumberDto response1 = callCreateNumberPutOk(request1);

    final CreateNumberRequest.Builder request2 = createCorrectCreateNumberRequest(number2, response1.getToken());
    final NumberDto response2 = callCreateNumberPutOk(request2);

    final CreateNumberRequest.Builder request3 = createCorrectCreateNumberRequest(number3, response2.getToken());
    final NumberDto response3 = callCreateNumberPutOk(request3);

    Assertions.assertThat(numberRepository.findAll()).hasSize(3);

    final long respNumberId1 = response1.getNumberId();
    final long respNumberId2 = response2.getNumberId();
    final long respNumberId3 = response3.getNumberId();

    if (numberRepository.findById(respNumberId1).isPresent() && numberRepository.findById(respNumberId2).isPresent()
        && numberRepository.findById(respNumberId3).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respNumberId1).get().getNumber()).isEqualTo(number1);
      Assertions.assertThat(numberRepository.findById(respNumberId2).get().getNumber()).isEqualTo(number2);
      Assertions.assertThat(numberRepository.findById(respNumberId3).get().getNumber()).isEqualTo(number3);
    }
  }

  @Test
  public void saveNumber_ShouldWork_SaveManyNumbersForTreeUsers() {
    final List<String> tokenList = createTreeCorrectUsersAndActivateThem();

    final long user1Number1 = 10L;
    final long user1Number2 = 20L;
    final long user1Number3 = 30L;

    final long user2Number1 = 40L;
    final long user2Number2 = 50L;
    final long user2Number3 = 60L;

    final long user3Number1 = 70L;
    final long user3Number2 = 80L;
    final long user3Number3 = 90L;

    Assertions.assertThat(userRepository.findAll()).hasSize(3);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(3);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);

    CreateNumberRequest.Builder user1Request1 = createCorrectCreateNumberRequest(user1Number1, tokenList.get(0));
    final NumberDto user1Response1 = callCreateNumberPutOk(user1Request1);
    CreateNumberRequest.Builder user1Request2 = createCorrectCreateNumberRequest(user1Number2,
        user1Response1.getToken());
    final NumberDto user1Response2 = callCreateNumberPutOk(user1Request2);
    CreateNumberRequest.Builder user1Request3 = createCorrectCreateNumberRequest(user1Number3,
        user1Response2.getToken());
    final NumberDto user1Response3 = callCreateNumberPutOk(user1Request3);

    CreateNumberRequest.Builder user2Request1 = createCorrectCreateNumberRequest(user2Number1, tokenList.get(1));
    final NumberDto user2Response1 = callCreateNumberPutOk(user2Request1);
    CreateNumberRequest.Builder user2Request2 = createCorrectCreateNumberRequest(user2Number2,
        user2Response1.getToken());
    final NumberDto user2Response2 = callCreateNumberPutOk(user2Request2);
    CreateNumberRequest.Builder user2Request3 = createCorrectCreateNumberRequest(user2Number3,
        user2Response2.getToken());
    final NumberDto user2Response3 = callCreateNumberPutOk(user2Request3);

    CreateNumberRequest.Builder user3Request1 = createCorrectCreateNumberRequest(user3Number1, tokenList.get(2));
    final NumberDto user3Response1 = callCreateNumberPutOk(user3Request1);
    CreateNumberRequest.Builder user3Request2 = createCorrectCreateNumberRequest(user3Number2,
        user3Response1.getToken());
    final NumberDto user3Response2 = callCreateNumberPutOk(user3Request2);
    CreateNumberRequest.Builder user3Request3 = createCorrectCreateNumberRequest(user3Number3,
        user3Response2.getToken());
    final NumberDto user3Response3 = callCreateNumberPutOk(user3Request3);

    Assertions.assertThat(userRepository.findAll()).hasSize(3);
    Assertions.assertThat(tokenRepository.findAll()).hasSize(3);
    Assertions.assertThat(numberRepository.findAll()).hasSize(9);

    final long respUser1NumberId1 = user1Response1.getNumberId();
    final long respUser1NumberId2 = user1Response2.getNumberId();
    final long respUser1NumberId3 = user1Response3.getNumberId();

    final long respUser2NumberId1 = user2Response1.getNumberId();
    final long respUser2NumberId2 = user2Response2.getNumberId();
    final long respUser2NumberId3 = user2Response3.getNumberId();

    final long respUser3NumberId1 = user3Response1.getNumberId();
    final long respUser3NumberId2 = user3Response2.getNumberId();
    final long respUser3NumberId3 = user3Response3.getNumberId();

    if (numberRepository.findById(respUser1NumberId1).isPresent()
        && numberRepository.findById(respUser1NumberId2).isPresent()
        && numberRepository.findById(respUser1NumberId3).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respUser1NumberId1).get().getNumber()).isEqualTo(user1Number1);
      Assertions.assertThat(numberRepository.findById(respUser1NumberId2).get().getNumber()).isEqualTo(user1Number2);
      Assertions.assertThat(numberRepository.findById(respUser1NumberId3).get().getNumber()).isEqualTo(user1Number3);
    }

    if (numberRepository.findById(respUser2NumberId1).isPresent()
        && numberRepository.findById(respUser2NumberId2).isPresent()
        && numberRepository.findById(respUser2NumberId3).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respUser2NumberId1).get().getNumber()).isEqualTo(user2Number1);
      Assertions.assertThat(numberRepository.findById(respUser2NumberId2).get().getNumber()).isEqualTo(user2Number2);
      Assertions.assertThat(numberRepository.findById(respUser2NumberId3).get().getNumber()).isEqualTo(user2Number3);
    }

    if (numberRepository.findById(respUser3NumberId1).isPresent()
        && numberRepository.findById(respUser3NumberId2).isPresent()
        && numberRepository.findById(respUser3NumberId3).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respUser3NumberId1).get().getNumber()).isEqualTo(user3Number1);
      Assertions.assertThat(numberRepository.findById(respUser3NumberId2).get().getNumber()).isEqualTo(user3Number2);
      Assertions.assertThat(numberRepository.findById(respUser3NumberId3).get().getNumber()).isEqualTo(user3Number3);
    }
  }

  @Test
  public void saveNumber_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    createCorrectUserAndActivateUser();
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";
    final long number = 1L;
    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(number, token);

    final ErrorInfo responseErr = callCreateNumberPutError(HttpStatus.BAD_REQUEST, request);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "saveNumber :: User not found with token: " + token);
  }

  @Test
  public void saveNumber_ShouldFail_TokenNull() {
    createCorrectUserAndActivateUser();
    final long number = 1L;
    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(number, null);

    final ErrorInfo responseErr = callCreateNumberPutError(HttpStatus.LENGTH_REQUIRED, request);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "Number token may not be null or empty");
  }

  @Test
  public void saveNumber_ShouldFail_TokenIsNullInString() {
    createCorrectUserAndActivateUser();
    final long number = 1L;
    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(number, "null");

    final ErrorInfo responseErr = callCreateNumberPutError(HttpStatus.LENGTH_REQUIRED, request);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "Token must be 36 characters");
  }

  @Test
  public void saveNumber_ShouldFail_TokenTooLong() {
    createCorrectUserAndActivateUser();
    final long number = 1L;
    final String token = "tokentokentokentokentokentokentokentoken";
    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(number, token);

    final ErrorInfo responseErr = callCreateNumberPutError(HttpStatus.LENGTH_REQUIRED, request);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "Token must be 36 characters");
  }

  @Test
  public void saveNumber_ShouldFail_TokenTooShort() {
    createCorrectUserAndActivateUser();
    final long number = 1L;
    final String token = "token";
    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(number, token);

    final ErrorInfo responseErr = callCreateNumberPutError(HttpStatus.LENGTH_REQUIRED, request);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "Token must be 36 characters");
  }

  @Test
  public void saveNumber_ShouldFail_NumberNull() {
    final String token = createCorrectUserAndActivateUser();
    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(null, token);

    final ErrorInfo responseErr = callCreateNumberPutError(HttpStatus.NOT_ACCEPTABLE, request);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.NUMBER_NULL,
        "saveNumber :: number is null with token: " + token);
  }

  @Test
  public void getNumber_ShouldWork_GetANumber() {
    final String token = createCorrectUserAndActivateUser();
    final long number = 9L;

    final CreateNumberRequest.Builder request = createCorrectCreateNumberRequest(number, token);
    final NumberDto response = callCreateNumberPutOk(request);

    final Long respNumberId = response.getNumberId();
    final String respToken = response.getToken();

    Assertions.assertThat(numberRepository.findAll()).hasSize(1);

    if (numberRepository.findById(respNumberId).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respNumberId).get().getNumber()).isEqualTo(number);
    }

    final NumberDto responseNumb = callGetNumberGetOk(respNumberId, respToken);
    Assertions.assertThat(responseNumb).isNotNull();
    Assertions.assertThat(responseNumb.getNumber()).isEqualTo(number);
  }

  @Test
  public void getNumber_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    createCorrectUserAndActivateUser();
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";
    final long numberId = 1L;

    final ErrorInfo responseErr = callGetNumberGetError(HttpStatus.BAD_REQUEST, numberId, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "getNumber :: User not found with token: " + token);
  }

  @Test
  public void getNumber_ShouldFail_TokenNull() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;

    final ErrorInfo responseErr = callGetNumberGetError(HttpStatus.BAD_REQUEST, numberId, null);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "getNumber :: Token is null or empty with token: " + null);
  }

  @Test
  public void getNumber_ShouldFail_TokenIsNullInString() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;

    final ErrorInfo responseErr = callGetNumberGetError(HttpStatus.BAD_REQUEST, numberId, "null");
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "getNumber :: Token is null or empty with token: null");
  }

  @Test
  public void getNumber_ShouldFail_TokenTooLong() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;
    final String token = "tokentokentokentokentokentokentokentoken";

    final ErrorInfo responseErr = callGetNumberGetError(HttpStatus.BAD_REQUEST, numberId, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getNumber :: Token must be 36 characters with token: " + token);
  }

  @Test
  public void getNumber_ShouldFail_TokenTooShort() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;
    final String token = "token";

    final ErrorInfo responseErr = callGetNumberGetError(HttpStatus.BAD_REQUEST, numberId, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getNumber :: Token must be 36 characters with token: " + token);
  }

  @Test
  public void getNumber_ShouldFail_NullNumberId() {
    final String token = createCorrectUserAndActivateUser();

    final ErrorInfo responseErr = callGetNumberGetError(HttpStatus.BAD_REQUEST, null, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.VALIDATION_ERROR_REQUEST_PARAM,
        "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; "
            + "nested exception is java.lang.NumberFormatException: For input string: \"null\"");
  }

  @Test
  public void getNumber_ShouldFail_IdNumberDoesNotExist() {
    final String token = createCorrectUserAndActivateUser();
    final long numberId = 15L;

    final ErrorInfo responseErr = callGetNumberGetError(HttpStatus.NOT_FOUND, numberId, token);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.NUMBER_NOT_FOUND,
        "Number with id = '" + numberId + "' does not exist");
  }

  @Test
  public void deleteNumber_ShouldWork_DeleteANumber() {
    final String token = createCorrectUserAndActivateUser();
    final long number = 1L;

    final CreateNumberRequest.Builder requestCreateNumberBuilder = createCorrectCreateNumberRequest(number, token);
    final NumberDto response = callCreateNumberPutOk(requestCreateNumberBuilder);
    final long respNumberId = response.getNumberId();

    Assertions.assertThat(numberRepository.findAll()).hasSize(1);

    if (numberRepository.findById(respNumberId).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respNumberId).get().getNumber()).isEqualTo(number);
    }

    final CreateNumberRequest.Builder correctDeleteNumberRequestBuilder = createCorrectDeleteNumberRequest(respNumberId,
        response.getToken());
    final GenericMessageResponse respDeleted = callCreateDeleteNumberPutOk(correctDeleteNumberRequestBuilder);

    Assertions.assertThat(numberRepository.findAll()).hasSize(1);
    Assertions.assertThat(respDeleted.getMessage()).isEqualTo("Number deleted");
    Assertions.assertThat(respDeleted.getToken()).isNotEqualTo(response.getToken());

    Number fetchDeletedNumber = null;
    if (numberRepository.findById(respNumberId).isPresent()) {
      fetchDeletedNumber = numberRepository.findById(respNumberId).get();
    }

    assert fetchDeletedNumber != null;
    Assertions.assertThat(fetchDeletedNumber.getDeletedNumber()).isEqualTo(true);
    Assertions.assertThat(fetchDeletedNumber.getId()).isEqualTo(respNumberId);
    Assertions.assertThat(fetchDeletedNumber.getNumber()).isEqualTo(number);
    Assertions.assertThat(fetchDeletedNumber.getUser()).isNotEqualTo(null);
  }

  @Test
  public void deleteNumber_ShouldFail_NumberIdDoesNotExist() {
    final String token = createCorrectUserAndActivateUser();
    final long nonExistingNumberId = 999L;
    final CreateNumberRequest.Builder correctDeleteNumberRequest = createCorrectDeleteNumberRequest(nonExistingNumberId,
        token);

    final ErrorInfo responseErr = callDeleteNumberPutError(HttpStatus.NOT_FOUND, correctDeleteNumberRequest);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.NUMBER_NOT_FOUND,
        "Number with id = '" + nonExistingNumberId + "' does not exist");
  }

  @Test
  public void deleteNumber_ShouldFail_NullNumberId() {
    final String token = createCorrectUserAndActivateUser();
    final CreateNumberRequest.Builder correctDeleteNumberRequest = createCorrectDeleteNumberRequest(null,
        token);

    final ErrorInfo responseErr = callDeleteNumberPutError(HttpStatus.NOT_ACCEPTABLE, correctDeleteNumberRequest);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.NUMBER_NULL,
        "deleteNumber :: numberId is null with token: " + token);
  }

  @Test
  public void deleteNumber_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    createCorrectUserAndActivateUser();
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";
    final long numberId = 1L;
    final CreateNumberRequest.Builder correctDeleteNumberRequest = createCorrectDeleteNumberRequest(numberId, token);

    ErrorInfo responseErr = callDeleteNumberPutError(HttpStatus.BAD_REQUEST, correctDeleteNumberRequest);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "deleteNumber :: User not found with token: " + token);
  }

  @Test
  public void deleteNumber_ShouldFail_TokenNull() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;
    final CreateNumberRequest.Builder correctDeleteNumberRequest = createCorrectDeleteNumberRequest(numberId,
        null);

    final ErrorInfo responseErr = callDeleteNumberPutError(HttpStatus.LENGTH_REQUIRED, correctDeleteNumberRequest);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "Number token may not be null or empty");
  }

  @Test
  public void deleteNumber_ShouldFail_TokenIsNullInString() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;
    final CreateNumberRequest.Builder correctDeleteNumberRequest = createCorrectDeleteNumberRequest(numberId,
        "null");

    final ErrorInfo responseErr = callDeleteNumberPutError(HttpStatus.LENGTH_REQUIRED, correctDeleteNumberRequest);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "Token must be 36 characters");
  }

  @Test
  public void deleteNumber_ShouldFail_TokenTooLong() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;
    final String token = "tokentokentokentokentokentokentokentoken";
    final CreateNumberRequest.Builder correctDeleteNumberRequest = createCorrectDeleteNumberRequest(numberId, token);

    final ErrorInfo responseErr = callDeleteNumberPutError(HttpStatus.LENGTH_REQUIRED, correctDeleteNumberRequest);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "Token must be 36 characters");
  }

  @Test
  public void deleteNumber_ShouldFail_TokenTooShort() {
    createCorrectUserAndActivateUser();
    final long numberId = 1L;
    final String token = "token";
    final CreateNumberRequest.Builder correctDeleteNumberRequest = createCorrectDeleteNumberRequest(numberId, token);

    final ErrorInfo responseErr = callDeleteNumberPutError(HttpStatus.LENGTH_REQUIRED, correctDeleteNumberRequest);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "Token must be 36 characters");
  }

  @Test
  public void getCountUserNumbers_ShouldWork_CountAllNumbersOfAUser() {
    final String token = createCorrectUserAndActivateUser();
    final long number1 = 1L;
    final long number2 = 2L;

    final CreateNumberRequest.Builder request1 = createCorrectCreateNumberRequest(number1, token);
    final NumberDto response1 = callCreateNumberPutOk(request1);

    final CreateNumberRequest.Builder request2 = createCorrectCreateNumberRequest(number2, response1.getToken());
    final NumberDto response2 = callCreateNumberPutOk(request2);
    Assertions.assertThat(numberRepository.findAll()).hasSize(2);

    final long respNumberId1 = response1.getNumberId();
    final long respNumberId2 = response2.getNumberId();

    if (numberRepository.findById(respNumberId1).isPresent() && numberRepository.findById(respNumberId2).isPresent()) {
      Assertions.assertThat(numberRepository.findById(respNumberId1).get().getNumber()).isEqualTo(number1);
      Assertions.assertThat(numberRepository.findById(respNumberId2).get().getNumber()).isEqualTo(number2);
    }

    final GenericMessageResponse responseCount = callGetCountAllUserNumbersGetOk(response2.getToken());
    Assertions.assertThat(numberRepository.count()).isEqualTo(Long.valueOf(responseCount.getMessage()));
    Assertions.assertThat(responseCount.getToken()).isNotEqualTo(response2.getToken());
  }

  @Test
  public void getCountUserNumbers_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    createCorrectUserAndActivateUser();
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";

    final ErrorInfo responseErr = callGetCountAllUserNumbersGetError(HttpStatus.BAD_REQUEST, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "getCountUserNumbers :: User not found with token: " + token);
  }

  @Test
  public void getCountUserNumbers_ShouldFail_TokenNull() {
    createCorrectUserAndActivateUser();

    final ErrorInfo responseErr = callGetCountAllUserNumbersGetError(HttpStatus.BAD_REQUEST, null);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "getCountUserNumbers :: Token is null or empty with token: null");
  }

  @Test
  public void getCountUserNumbers_ShouldFail_TokenIsNullInString() {
    createCorrectUserAndActivateUser();

    final ErrorInfo responseErr = callGetCountAllUserNumbersGetError(HttpStatus.BAD_REQUEST, "null");
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "getCountUserNumbers :: Token is null or empty with token: null");
  }

  @Test
  public void getCountUserNumbers_ShouldFail_TokenTooLong() {
    createCorrectUserAndActivateUser();
    final String token = "tokentokentokentokentokentokentokentoken";

    final ErrorInfo responseErr = callGetCountAllUserNumbersGetError(HttpStatus.BAD_REQUEST, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getCountUserNumbers :: Token must be 36 characters with token: " + token);
  }

  @Test
  public void getCountUserNumbers_ShouldFail_TokenTooShort() {
    createCorrectUserAndActivateUser();
    final String token = "token";

    final ErrorInfo responseErr = callGetCountAllUserNumbersGetError(HttpStatus.BAD_REQUEST, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getCountUserNumbers :: Token must be 36 characters with token: " + token);
  }

  @Test
  public void getUserNumbers_ShouldWork_GetListOfAllItems() {
    final String token = createCorrectUserAndActivateUser();
    final long number1 = 1L;
    final long number2 = 2L;
    final long number3 = 3L;

    final CreateNumberRequest.Builder request1 = createCorrectCreateNumberRequest(number1, token);
    final NumberDto response1 = callCreateNumberPutOk(request1);
    final CreateNumberRequest.Builder request2 = createCorrectCreateNumberRequest(number2, response1.getToken());
    final NumberDto response2 = callCreateNumberPutOk(request2);
    final CreateNumberRequest.Builder request3 = createCorrectCreateNumberRequest(number3, response2.getToken());
    final  NumberDto response3 = callCreateNumberPutOk(request3);

    final long respNumberId1 = response1.getNumberId();
    final long respNumberId2 = response2.getNumberId();
    final long respNumberId3 = response3.getNumberId();

    final List<NumberDto> respList = callGetUserNumbersGetOk(0, response3.getToken());

    Assertions.assertThat(numberRepository.findAll()).hasSize(3);
    Assertions.assertThat(respList.size()).isEqualTo(3);

    Assertions.assertThat(respList.get(0).getNumberId()).isEqualTo(respNumberId1);
    Assertions.assertThat(respList.get(0).getNumber()).isEqualTo(number1);
    Assertions.assertThat(respList.get(1).getNumberId()).isEqualTo(respNumberId2);
    Assertions.assertThat(respList.get(1).getNumber()).isEqualTo(number2);
    Assertions.assertThat(respList.get(2).getNumberId()).isEqualTo(respNumberId3);
    Assertions.assertThat(respList.get(2).getNumber()).isEqualTo(number3);
  }

  @Test
  public void getUserNumbers_ShouldWork_Make30NumbersAndGetTwoPages() {
    String currentToken = createCorrectUserAndActivateUser();
    CreateNumberRequest.Builder request;
    NumberDto response;
    long number = 0;

    for (int i = 0; i < 30; i++) {
      number += 1;
      request = createCorrectCreateNumberRequest(number, currentToken);
      response = callCreateNumberPutOk(request);
      currentToken = response.getToken();
    }

    Assertions.assertThat(numberRepository.findAll()).hasSize(30);

    List<NumberDto> respList = callGetUserNumbersGetOk(0, currentToken);
    Assertions.assertThat(respList.size()).isEqualTo(25);
    Assert.assertTrue(respList.stream().anyMatch(numb -> numb.getNumber() == 1L));
    Assert.assertTrue(respList.stream().anyMatch(numb -> numb.getNumber() == 25L));

    respList = callGetUserNumbersGetOk(1, respList.get(0).getToken());
    Assertions.assertThat(respList.size()).isEqualTo(5);
    Assert.assertTrue(respList.stream().anyMatch(numb -> numb.getNumber() == 26L));
    Assert.assertTrue(respList.stream().anyMatch(numb -> numb.getNumber() == 30L));
  }

  @Test
  public void getUserNumbers_ShouldWork_GetListOfAllItemsWithEmptyList() {
    final String token = createCorrectUserAndActivateUser();
    final int indexPage = 0;

    final List<NumberDto> response = callGetUserNumbersGetOk(indexPage, token);
    Assertions.assertThat(response.size()).isEqualTo(0);
  }

  @Test
  public void getUserNumbers_ShouldFail_IndexPageIsLargerThanInteger() {
    final String token = createCorrectUserAndActivateUser();
    final Long indexPage = 999999999999999999L;

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.LENGTH_REQUIRED, indexPage, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.NUMBER_LENGTH_MISMATCH,
        "getUserNumbers :: indexPage must at most be 2147483647 characters with token: " + token);
  }

  @Test
  public void getUserNumbers_ShouldFail_IndexPageIsSmallerThanInteger() {
    final String token = createCorrectUserAndActivateUser();
    final Long indexPage = -999999999999999999L;

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.LENGTH_REQUIRED, indexPage, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.NUMBER_LENGTH_MISMATCH,
        "getUserNumbers :: indexPage must at least be -2147483648 characters with token: " + token);
  }

  @Test
  public void getUserNumbers_ShouldFail_IndexPageIsNull() {
    final String token = createCorrectUserAndActivateUser();

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.BAD_REQUEST, null, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.VALIDATION_ERROR_REQUEST_PARAM,
        "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; "
            + "nested exception is java.lang.NumberFormatException: For input string: \"null\"");
  }

  @Test
  public void getUserNumbers_ShouldFail_TokenIsCorrectlyMadeButDoesNotExistInAnyUser() {
    createCorrectUserAndActivateUser();
    final String token = "0ac3b36d-183a-4f90-a47a-fa508cbe4c1a";
    final long indexPage = 0;

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.BAD_REQUEST, indexPage, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.USER_NOT_FOUND,
        "getUserNumbers :: User not found with token: " + token);
  }

  @Test
  public void getUserNumbers_ShouldFail_TokenNull() {
    createCorrectUserAndActivateUser();
    final long indexPage = 0;

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.BAD_REQUEST, indexPage, null);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "getUserNumbers :: Token is null or empty with token: null");
  }

  @Test
  public void getUserNumbers_ShouldFail_TokenIsNullInString() {
    createCorrectUserAndActivateUser();
    final long indexPage = 0;

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.BAD_REQUEST, indexPage, "null");
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_NULL_OR_EMPTY,
        "getUserNumbers :: Token is null or empty with token: null");
  }

  @Test
  public void getUserNumbers_ShouldFail_TokenTooLong() {
    createCorrectUserAndActivateUser();
    final String token = "tokentokentokentokentokentokentokentoken";
    final long indexPage = 0;

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.BAD_REQUEST, indexPage, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getUserNumbers :: Token must be 36 characters with token: " + token);
  }

  @Test
  public void getUserNumbers_ShouldFail_TokenTooShort() {
    createCorrectUserAndActivateUser();
    final String token = "token";
    final long indexPage = 0;

    final ErrorInfo responseErr = callGetUserNumbersGetError(HttpStatus.BAD_REQUEST, indexPage, token);
    Assertions.assertThat(numberRepository.findAll()).hasSize(0);
    TestCheckers.checkErrorResponse(responseErr, ErrorCode.TOKEN_LENGTH_MISMATCH,
        "getUserNumbers :: Token must be 36 characters with token: " + token);
  }

  private NumberDto callCreateNumberPutOk(final CreateNumberRequest.Builder request) {
    return httpPut(NUMBER_URL + "/v1/saveNumber", HttpStatus.OK, request.build(), NumberDto.class);
  }

  private ErrorInfo callCreateNumberPutError(final HttpStatus expectedStatus,
      final CreateNumberRequest.Builder request) {
    return httpPutError(NUMBER_URL + "/v1/saveNumber", expectedStatus, request.build(), ErrorInfo.class);
  }

  private NumberDto callGetNumberGetOk(final long numberId, final String token) {
    return httpGet(NUMBER_URL + "/v1/getNumber/" + token + "/" + numberId, HttpStatus.OK, NumberDto.class);
  }

  private ErrorInfo callGetNumberGetError(final HttpStatus expectedStatus, final Long numberId, final String token) {
    return httpGetError(NUMBER_URL + "/v1/getNumber/" + token + "/" + numberId, expectedStatus, ErrorInfo.class);
  }

  private GenericMessageResponse callGetCountAllUserNumbersGetOk(final String token) {
    return httpGet(NUMBER_URL + "/v1/countUserNumbers/" + token, HttpStatus.OK, GenericMessageResponse.class);
  }

  private ErrorInfo callGetCountAllUserNumbersGetError(final HttpStatus expectedStatus, final String token) {
    return httpGetError(NUMBER_URL + "/v1/countUserNumbers/" + token, expectedStatus, ErrorInfo.class);
  }

  private GenericMessageResponse callCreateDeleteNumberPutOk(final CreateNumberRequest.Builder request) {
    return httpPut(NUMBER_URL + "/v1/deleteNumber", HttpStatus.OK, request.build(), GenericMessageResponse.class);
  }

  private List<NumberDto> callGetUserNumbersGetOk(final Integer indexPage, final String token) {
    return httpGetList(NUMBER_URL + "/v1/getUserNumbers/" + token + "/" + indexPage, HttpStatus.OK,
        NumberDto.class);
  }

  private ErrorInfo callGetUserNumbersGetError(final HttpStatus expectedStatus, final Long indexPage,
      final String token) {
    return httpGetError(NUMBER_URL + "/v1/getUserNumbers/" + token + "/" + indexPage, expectedStatus,
        ErrorInfo.class);
  }

  private UserDto callCreateUserPutOk(final CreateUserRequest.Builder request) {
    return httpPut(USER_URL + "/v1/upsertUser", HttpStatus.OK, request.build(), UserDto.class);
  }

  private ErrorInfo callDeleteNumberPutError(final HttpStatus expectedStatus,
      final CreateNumberRequest.Builder request) {
    return httpPutError(NUMBER_URL + "/v1/deleteNumber/", expectedStatus, request.build(), ErrorInfo.class);
  }

  private CreateNumberRequest.Builder createCorrectCreateNumberRequest(final Long number, final String token) {
    return CreateNumberRequest.newBuilder()
        .withNumber(number)
        .withToken(token);
  }

  private CreateNumberRequest.Builder createCorrectDeleteNumberRequest(final Long number, final String token) {
    return CreateNumberRequest.newBuilder()
        .withNumberId(number)
        .withToken(token);
  }

  private String createCorrectUserAndActivateUser() {
    CreateUserRequest.Builder userRequest = TestUsers.createCorrectUserRequestBuilder();

    final UserDto userResponse = callCreateUserPutOk(userRequest);

    Users user = userRepository.findByEmail(userRequest.build().getEmail());
    user.setEnabled(true);
    userRepository.save(user);

    return userResponse.getToken();
  }

  private List<String> createTreeCorrectUsersAndActivateThem() {
    final CreateUserRequest.Builder userRequest1 = TestUsers.createCorrectUserRequestBuilder();
    CreateUserRequest.Builder userRequest2 = TestUsers.createCorrectUserRequestBuilder();
    CreateUserRequest.Builder userRequest3 = TestUsers.createCorrectUserRequestBuilder();

    userRequest2.withEmail("user2@email.com").withUserName("User2IsTheBest");
    userRequest3.withEmail("user3@email.com").withUserName("User3IsBetter");

    final String user1Token = callCreateUserPutOk(userRequest1).getToken();
    final String user2Token = callCreateUserPutOk(userRequest2).getToken();
    final String user3Token = callCreateUserPutOk(userRequest3).getToken();

    List<String> tokenList = new ArrayList<>();
    tokenList.add(user1Token);
    tokenList.add(user2Token);
    tokenList.add(user3Token);

    Users user1 = userRepository.findByEmail(userRequest1.build().getEmail());
    Users user2 = userRepository.findByEmail(userRequest2.build().getEmail());
    Users user3 = userRepository.findByEmail(userRequest3.build().getEmail());

    user1.setEnabled(true);
    user2.setEnabled(true);
    user3.setEnabled(true);

    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);

    return tokenList;
  }
}
