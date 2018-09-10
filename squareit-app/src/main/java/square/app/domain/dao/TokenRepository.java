package square.app.domain.dao;

import org.springframework.data.repository.CrudRepository;

import square.app.domain.jpa.VerificationToken;

public interface TokenRepository extends CrudRepository<VerificationToken, Long> {
}
