package square.api.domain.models.number;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import square.api.domain.constants.Constraint;
import square.api.domain.util.JsonUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = CreateNumberRequest.Builder.class)
public class CreateNumberRequest {

  @Max(value = Long.MAX_VALUE, message = "NumberId cannot be greater than " + Long.MAX_VALUE)
  @Min(value = Long.MIN_VALUE, message = "NumberId cannot be lower than " + Long.MIN_VALUE)
  private final Long numberId;

  @Max(value = Long.MAX_VALUE, message = "Number cannot be greater than " + Long.MAX_VALUE)
  @Min(value = Long.MIN_VALUE, message = "Number cannot be lower than " + Long.MIN_VALUE)
  private final Long number;

  @NotEmpty(message = "Number token may not be null or empty")
  @Size(max = Constraint.MAX_MIN_TOKEN, min = Constraint.MAX_MIN_TOKEN, message = "Token must be 36 characters")
  private final String token;

  private CreateNumberRequest(Builder builder) {
    numberId = builder.numberId;
    number = builder.number;
    token = builder.token;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Long getNumberId() {
    return numberId;
  }

  public Long getNumber() {
    return number;
  }

  public String getToken() {
    return token;
  }

  @Override
  public String toString() {
    return JsonUtil.writeObjectAsString(this);
  }

  public static final class Builder {
    private Long numberId;
    private Long number;
    private String token;

    private Builder() {
    }

    public Builder withNumberId(Long val) {
      numberId = val;
      return this;
    }

    public Builder withNumber(Long val) {
      number = val;
      return this;
    }

    public Builder withToken(String val) {
      token = val;
      return this;
    }

    public CreateNumberRequest build() {
      return new CreateNumberRequest(this);
    }
  }
}
