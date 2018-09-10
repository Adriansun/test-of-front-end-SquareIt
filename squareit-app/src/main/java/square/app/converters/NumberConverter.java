package square.app.converters;

import java.util.List;
import java.util.stream.Collectors;

import square.api.domain.models.number.NumberDto;

import square.app.domain.jpa.Number;

public final class NumberConverter {

  private NumberConverter() {
    throw new IllegalStateException("NumberConverter class :: Cannot be instantiated");
  }

  /**
   * Squares the number.
   *
   * @param number original number
   * @return squared number
   */
  private static long squareNumber(final long number) {
    return (long) Math.pow(number, 2);
  }

  /**
   * Number to NumberDto.
   *
   * @param number number entity
   * @param token  new token
   * @return NumberDto
   */
  public static NumberDto toNumberResponse(final Number number, final String token) {
    return NumberDto.newBuilder()
        .withNumberId(number.getId())
        .withNumber(number.getNumber())
        .withNumberSquared(squareNumber(number.getNumber()))
        .withToken(token)
        .build();
  }

  /**
   * NumberList to NumberResponseList converter.
   *
   * @param numberList list of numbers
   * @return list of numberResponses
   */
  public static List<NumberDto> toNumberResponseList(final List<Number> numberList, final String token) {
    return numberList.parallelStream()
        .map(number -> toNumberResponse(number, token))
        .collect(Collectors.toList());
  }
}
