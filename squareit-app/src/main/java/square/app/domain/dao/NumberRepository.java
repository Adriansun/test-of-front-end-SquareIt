package square.app.domain.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import square.app.domain.jpa.Number;
import square.app.domain.jpa.Users;

public interface NumberRepository extends CrudRepository<Number, Long> {

  Number findOneNumberByIdAndUser(@Param("id") final Long id, @Param("user") final Users user);

  Page<Number> findAllNumbers(@Param("user") final Users user, final Pageable pageRequest);

  long countUserNumbers(@Param("user") final Users user);

}
