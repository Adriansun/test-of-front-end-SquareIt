package square.app.restcontroller.baserestcontroller;

import org.slf4j.MDC;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import square.api.domain.errors.ErrorCode;
import square.api.domain.errors.ErrorInfo;
import square.api.domain.headersdefinition.HeadersDefinition;

import square.app.domain.jpa.Number;
import square.app.exceptions.EmailExistsException;
import square.app.exceptions.ObjectNotFoundException;
import square.app.exceptions.TimeException;
import square.app.exceptions.TokenException;
import square.app.exceptions.TokenExpiredException;
import square.app.exceptions.TokenLengthException;
import square.app.exceptions.TokenMismatchException;
import square.app.exceptions.UserAlreadyActivatedException;
import square.app.exceptions.UserDeletedException;
import square.app.exceptions.UserExistsException;
import square.app.exceptions.UserNotActivatedException;
import square.app.exceptions.UserNotFoundException;

public class BaseRestController {

  /**
   * EmailExistsException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({EmailExistsException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalEmailExistsException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.EMAIL_ALREADY_EXISTS)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * UserDeletedException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({UserDeletedException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorInfo handleIllegalUserDeletedException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.USER_DELETED)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * UserAlreadyActivatedException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({UserAlreadyActivatedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalUserAlreadyRegisteredException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.USER_ALREADY_ACTIVATED)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * UserNotActivatedException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({UserNotActivatedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalUserNotActivatedException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.USER_NOT_ACTIVATED)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * UserNotFoundException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({UserNotFoundException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalUserNotFoundException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.USER_NOT_FOUND)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * UserNotFoundException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({UserExistsException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalUserExistExceptionException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.USER_ALREADY_EXISTS)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * TimeException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({TimeException.class})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ErrorInfo handleIllegalTimeException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.TOKEN_TIME_NULL)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * TokenException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({TokenException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalTokenException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.TOKEN_NULL_OR_EMPTY)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * TokenLengthException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({TokenLengthException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalTokenLengthException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.TOKEN_LENGTH_MISMATCH)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * TokenMismatchException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({TokenMismatchException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalTokenMismatchException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.TOKEN_MISMATCH)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * TokenExpiredException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({TokenExpiredException.class})
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ErrorInfo handleIllegalTokenExpiredException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.TOKEN_EXPIRED)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * IllegalArgumentException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({IllegalArgumentException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorInfo handleIllegalArgumentException(final Exception ex) {
    return ErrorInfo.newErrorInfo()
        .withErrorCode(ErrorCode.VALIDATION_ERROR_REQUEST_PARAM)
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }

  /**
   * ObjectNotFoundException.
   *
   * @param ex ex
   * @return ErrorInfo
   */
  @ExceptionHandler({ObjectNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorInfo handleNotFound(final ObjectNotFoundException ex) {
    if (ex.getEntityNotFound().equals(Number.class)) {
      return ErrorInfo.newErrorInfo()
          .withErrorCode(ErrorCode.NUMBER_NOT_FOUND)
          .withErrorDescription(ex.getMessage())
          .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
          .build();
    }

    return ErrorInfo.newErrorInfo()
        .withErrorDescription(ex.getMessage())
        .withReferenceId(MDC.get(HeadersDefinition.BREAD_CRUMB_ID))
        .build();
  }
}
