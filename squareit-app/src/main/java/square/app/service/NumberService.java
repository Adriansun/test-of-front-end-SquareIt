package square.app.service;

import java.util.List;

import square.api.domain.models.number.CreateNumberRequest;

import square.app.domain.jpa.Number;
import square.app.domain.jpa.Users;

public interface NumberService {

  Number saveNumber(final CreateNumberRequest createNumberRequest, final Users user);

  Number getNumberById(final Long numberId, final Users user);

  void deleteIdNumber(final long numberId, final Users user);

  long countUserNumbers(final Users user);

  List<Number> getAllUserNumbers(final Users user, final Integer indexPage);

}
