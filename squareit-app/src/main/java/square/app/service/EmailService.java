package square.app.service;

import square.app.domain.jpa.Users;

public interface EmailService {

  void createRegistrationMail(final Users user, final Enum emailType);

}
