package square.app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import square.api.domain.models.number.CreateNumberRequest;

import square.app.domain.dao.NumberRepository;
import square.app.domain.jpa.Number;
import square.app.domain.jpa.Users;
import square.app.errorhandling.ErrorHandling;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class NumberServiceImpl implements NumberService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NumberServiceImpl.class);

  private final NumberRepository numberRepository;

  @Value("${database.default-fetch-limit}")
  private int defaultFetchLimit;

  /**
   * NumberServiceImpl.
   *
   * @param numberRepository numberRepository
   */
  @Autowired
  public NumberServiceImpl(final NumberRepository numberRepository) {
    this.numberRepository = numberRepository;
  }

  @Override
  public Number saveNumber(final CreateNumberRequest numberReq, final Users user) {
    LOGGER.info(">> NumberServiceImpl :: saveNumber >>");

    Number number = new Number();
    number.setUserIdUnique(user.getId());
    number.setNumber(numberReq.getNumber());
    number.setUser(user);
    number.setDeletedNumber(false);

    LOGGER.info("<< NumberServiceImpl :: saveNumber <<");
    return numberRepository.save(number);
  }

  @Override
  @Transactional(readOnly = true)
  public Number getNumberById(final Long numberId, final Users user) {
    LOGGER.info(">> NumberServiceImpl :: getNumberById >>");

    final Number number = numberRepository.findOneNumberByIdAndUser(numberId, user);
    ErrorHandling.errorHandlingNonExistingNumber(number, numberId, "NumberServiceImpl :: getNumberById");

    LOGGER.info("<< NumberServiceImpl :: getNumberById <<");
    return number;
  }

  @Override
  public void deleteIdNumber(final long numberId, final Users user) {
    LOGGER.info(">> NumberServiceImpl :: deleteIdNumber >>");

    final Number number = numberRepository.findOneNumberByIdAndUser(numberId, user);
    ErrorHandling.errorHandlingNonExistingNumber(number, numberId, "NumberServiceImpl :: deleteIdNumber");

    number.setDeletedNumber(true);
    numberRepository.save(number);

    LOGGER.info("<< NumberServiceImpl :: deleteIdNumber <<");
  }

  @Override
  @Transactional(readOnly = true)
  public long countUserNumbers(final Users user) {
    LOGGER.info(">><< NumberServiceImpl :: countUserNumbers >><<");
    return numberRepository.countUserNumbers(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Number> getAllUserNumbers(final Users user, final Integer indexPage) {
    LOGGER.info(">> NumberServiceImpl :: getAllUserNumbers >>");
    final PageRequest pageRequest = PageRequest.of(indexPage, defaultFetchLimit);

    LOGGER.info("<< NumberServiceImpl :: getAllUserNumbers <<");
    return numberRepository.findAllNumbers(user, pageRequest).getContent();
  }
}
