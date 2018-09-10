package square.api.domain.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = ErrorInfo.Builder.class)
public class ErrorInfo {

  private final ErrorCode errorCode;
  private final String errorDescription;
  private final String referenceId;

  private ErrorInfo(Builder builder) {
    this.errorCode = builder.errorCode;
    this.errorDescription = builder.errorDescription;
    this.referenceId = builder.referenceId;
  }

  public static Builder newErrorInfo() {
    return new Builder();
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public static final class Builder {
    private ErrorCode errorCode;
    private String errorDescription;
    private String referenceId;

    private Builder() {
    }

    public ErrorInfo build() {
      return new ErrorInfo(this);
    }

    public Builder withErrorCode(ErrorCode errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    public Builder withErrorDescription(String errorDescription) {
      this.errorDescription = errorDescription;
      return this;
    }

    public Builder withReferenceId(String referenceId) {
      this.referenceId = referenceId;
      return this;
    }
  }
}
