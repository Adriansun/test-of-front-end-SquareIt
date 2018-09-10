package square.app.domain.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import square.app.domain.jpa.Users;

public interface UserRepository extends CrudRepository<Users, Long> {

  Users findByUserName(final String userName);

  Users findByEmail(final String email);

  Users findUserByToken(@Param("token") final String token);

  boolean existsByUserName(final String userName);

  boolean existsByEmail(final String email);

  long countAllActiveUsers();

}
