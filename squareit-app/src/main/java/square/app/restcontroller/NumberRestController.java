package square.app.restcontroller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.headersdefinition.HeadersDefinition;
import square.api.domain.models.GenericMessageResponse;
import square.api.domain.models.number.CreateNumberRequest;
import square.api.domain.models.number.NumberDto;

import square.app.constants.TokenRequestType;
import square.app.converters.NumberConverter;
import square.app.domain.jpa.Number;
import square.app.domain.jpa.Users;
import square.app.errorhandling.ErrorHandling;
import square.app.exceptions.NumberException;
import square.app.exceptions.NumberLengthException;
import square.app.exceptions.NumberNotFoundException;
import square.app.exceptions.ObjectNotFoundException;
import square.app.restcontroller.baserestcontroller.BaseRestController;
import square.app.service.NumberService;
import square.app.service.UserService;

@RestController
@RequestMapping("/rest/number")
public class NumberRestController extends BaseRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NumberRestController.class);

  private final NumberService numberService;

  private final UserService userService;

  /**
   * NumberRestController rest service.
   *
   * @param numberService numberService
   * @param userService   userService
   */
  @Autowired
  public NumberRestController(final NumberService numberService, final UserService userService) {
    this.numberService = numberService;
    this.userService = userService;
  }

  /**
   * Save a number rest-service.
   *
   * @param request CreateNumberRequest
   * @return NumberDto
   */
  @ApiOperation(value = "saveNumber", notes = "Save a number.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Number saved"),
      @ApiResponse(code = 400, message = "Malformed request"),
      @ApiResponse(code = 404, message = "number not found")})
  @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/v1/saveNumber")
  @ResponseBody
  public NumberDto saveNumber(@RequestBody @Valid final CreateNumberRequest request) {
    try {
      LOGGER.info(">> saveNumber >>");
      LOGGER.info("Number request: {}", request.toString());

      String token = request.getToken();
      final Users user = checkUser("saveNumber", token);
      ErrorHandling.errorHandlingIntLongTypeValue(request.getNumber(), token, "number",
          "saveNumber", true);
      final Number number = numberService.saveNumber(request, user);
      token = userService.generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();

      LOGGER.info("Number: {}, with token: {}", number.toString(), token);
      LOGGER.info("<< saveNumber <<");
      return NumberConverter.toNumberResponse(number, token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot save number: {} with token {}. Exception {}.", request.getNumber(), request.getToken(), e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when saving number: " + request.getNumber() + ", with token: " + request.getToken() + ".", e);
      throw e;
    }
  }

  /**
   * Get a number rest-service.
   *
   * @param token    user token
   * @param numberId number id
   * @return NumberDto
   */
  @ApiOperation(value = "getNumber", notes = "Get a number.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Number found"),
      @ApiResponse(code = 404, message = "Number/User not found")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/getNumber/{token}/{numberId}")
  @ResponseBody
  public NumberDto getNumber(@PathVariable("token") String token, @PathVariable("numberId") final Long numberId) {
    try {
      LOGGER.info(">> getNumber >>");
      LOGGER.info("Token: {}, requesting numberId: {}", token, numberId);

      final Users user = checkUser("getNumber", token);
      ErrorHandling.errorHandlingIntLongTypeValue(numberId, token, "numberId",
          "getNumber", true);
      final Number number = numberService.getNumberById(numberId, user);
      token = userService.generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();

      LOGGER.info("Number: {}, with token: {}", number.toString(), token);
      LOGGER.info("<< getNumber <<");
      return NumberConverter.toNumberResponse(number, token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot fetch number with id: {} and token: {}. Exception {}", numberId, token, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when fetching id: " + numberId + " and token: " + token, e);
      throw e;
    }
  }

  /**
   * Delete a number rest-service.
   *
   * @param request request
   * @return genericMessageResponse
   */
  @ApiOperation(value = "deleteNumber", notes = "Delete a number by id.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Number id deleted"),
      @ApiResponse(code = 400, message = "Malformed request"),
      @ApiResponse(code = 404, message = "Number id/user not found")})
  @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/v1/deleteNumber")
  @ResponseBody
  public GenericMessageResponse deleteNumber(@RequestBody @Valid final CreateNumberRequest request) {
    try {
      LOGGER.info(">> deleteNumber >>");
      LOGGER.info("Number request: {}", request.toString());

      String token = request.getToken();
      Long numberId = request.getNumberId();

      final Users user = checkUser("deleteNumber", token);
      ErrorHandling.errorHandlingIntLongTypeValue(numberId, token, "numberId",
          "deleteNumber", true);

      numberService.deleteIdNumber(numberId, user);
      token = userService.generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();

      LOGGER.info("Number deleted, new token: {}", token);
      LOGGER.info("<< deleteNumber <<");
      return new GenericMessageResponse().withMessage("Number deleted", token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot delete ID-number: {}, for user with token: {}. Exception {}.", request.getNumberId(),
          request.getToken(), e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when deleting ID-number: {], for user with token: {}. Exception {}.", request.getNumberId(),
          request.getToken(), e);
      throw e;
    }
  }

  /**
   * Count all numbers of a user from the database rest-service.
   *
   * @return GenericMessageResponse
   */
  @ApiOperation(value = "getCountUserNumbers", notes = "Count all numbers of a user.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Count done"),
      @ApiResponse(code = 400, message = "Malformed request"),
      @ApiResponse(code = 404, message = "Numbers/user not found")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/countUserNumbers/{token}")
  @ResponseBody
  public GenericMessageResponse getCountUserNumbers(@PathVariable("token") String token) {
    try {
      LOGGER.info(">> getCountUserNumbers >>");
      LOGGER.info("Token of requesting user: {}", token);

      final Users user = checkUser("getCountUserNumbers", token);
      final long count = numberService.countUserNumbers(user);
      token = userService.generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();

      LOGGER.info("Number count: {}, new token: {}", count, token);
      LOGGER.info("<< getCountUserNumbers <<");
      return new GenericMessageResponse().withMessage(String.valueOf(count), token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot count numbers of user with token: {}. Exception {}.", token, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when counting numbers of user with token: {}. Exception {}.", token, e);
      throw e;
    }
  }

  /**
   * Get list of all number items in the database of a user rest-service.
   *
   * @param token user token
   * @return List of NumberDto
   */
  @ApiOperation(value = "getUserNumbers", notes = "Get a list of all number items from a user.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "List returned"),
      @ApiResponse(code = 400, message = "Malformed request"),
      @ApiResponse(code = 404, message = "List could not be created")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/getUserNumbers/{token}/{indexPage}")
  @ResponseBody
  public List<NumberDto> getUserNumbers(@PathVariable("token") String token,
      @PathVariable("indexPage") final Long indexPage) {
    try {
      LOGGER.info(">> getUserNumbers >>");
      LOGGER.info("Token of requesting user: {}", token);

      final Users user = checkUser("getUserNumbers", token);
      ErrorHandling.errorHandlingIntLongTypeValue(indexPage, token, "indexPage",
          "getUserNumbers", false);

      final List<Number> numberList = numberService.getAllUserNumbers(user, indexPage.intValue());
      token = userService.generateToken(user, TokenRequestType.UPDATE_TOKEN).getVerificationToken().getToken();

      LOGGER.info("Number list: {}, new token: {}", numberList, token);
      LOGGER.info("<< getUserNumbers <<");
      return NumberConverter.toNumberResponseList(numberList, token);
    } catch (IllegalArgumentException | ObjectNotFoundException e) {
      LOGGER.info("Cannot create list of all number items for user with token: {}. Exception {}.", token, e);
      throw e;
    } catch (RuntimeException e) {
      LOGGER.error("Error when creating list of all number items for user with token: {}. Exception {}.", token, e);
      throw e;
    }
  }

  private Users checkUser(final String methodName, final String token) {
    ErrorHandling.errorHandlingToken(token, methodName);
    Users user = userService.getUserByToken(token);
    ErrorHandling.userValidationChecks(user, true, token, methodName);

    return user;
  }

  /**
   * NumberNotFoundException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({NumberNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorInfo handleNumberNotFoundException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.NUMBER_NOT_FOUND)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * NumberException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({NumberException.class})
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ErrorInfo handleNumberException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.NUMBER_NULL)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * NumberLengthException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({NumberLengthException.class})
  @ResponseStatus(HttpStatus.LENGTH_REQUIRED)
  public ErrorInfo handleNumberLengthException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.NUMBER_LENGTH_MISMATCH)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * MethodArgumentNotValidException catches annotation errors in objects.
   *
   * @param ex ex
   * @return return
   */
  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.LENGTH_REQUIRED)
  public ErrorInfo handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
    ErrorCode errorCode = null;
    String errorDescription = Objects.requireNonNull(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());

    if (("NumberId cannot be greater than " + Long.MAX_VALUE).equals(errorDescription)
        || ("NumberId cannot be lower than " + Long.MIN_VALUE).equals(errorDescription)) {
      errorCode = ErrorCode.NUMBER_ID_LENGTH_MISMATCH;
    }

    if (("Number cannot be greater than " + Long.MAX_VALUE).equals(errorDescription)
        || ("Number cannot be lower than " + Long.MIN_VALUE).equals(errorDescription)) {
      errorCode = ErrorCode.NUMBER_LENGTH_MISMATCH;
    }

    if ("Number token may not be null or empty".equals(errorDescription)) {
      errorCode = ErrorCode.TOKEN_NULL_OR_EMPTY;
    } else if ("Token must be 36 characters".equals(errorDescription)) {
      errorCode = ErrorCode.TOKEN_LENGTH_MISMATCH;
    }

    return ErrorInfo.newErrorInfo()
        .withErrorCode(errorCode)
        .withErrorDescription(errorDescription)
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }
}
