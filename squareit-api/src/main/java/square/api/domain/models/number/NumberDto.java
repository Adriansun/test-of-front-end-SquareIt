package square.api.domain.models.number;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import square.api.domain.util.JsonUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = NumberDto.Builder.class)
public class NumberDto {

  private final long numberId;

  private final long number;

  private final long numberSquared;

  private final String token;

  private NumberDto(Builder builder) {
    numberId = builder.numberId;
    number = builder.number;
    numberSquared = builder.numberSquared;
    token = builder.token;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public long getNumberId() {
    return numberId;
  }

  public long getNumber() {
    return number;
  }

  public long getNumberSquared() {
    return numberSquared;
  }

  public String getToken() {
    return token;
  }

  @Override
  public String toString() {
    return JsonUtil.writeObjectAsString(this);
  }

  public static final class Builder {
    private long numberId;
    private long number;
    private long numberSquared;
    private String token;

    private Builder() {
    }

    public Builder withNumberId(long val) {
      numberId = val;
      return this;
    }

    public Builder withNumber(long val) {
      number = val;
      return this;
    }

    public Builder withNumberSquared(long val) {
      numberSquared = val;
      return this;
    }

    public Builder withToken(String val) {
      token = val;
      return this;
    }

    public NumberDto build() {
      return new NumberDto(this);
    }
  }
}
