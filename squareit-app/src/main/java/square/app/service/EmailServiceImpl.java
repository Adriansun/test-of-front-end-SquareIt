package square.app.service;

import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import square.app.constants.EmailTypes;
import square.app.domain.jpa.Users;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class EmailServiceImpl implements EmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

  private static final String DETERMINE_MAIL_TYPE_CASE =  ">> EmailServiceImpl :: determineMailType, case: {} >>";

  private static final String DETERMINE_MAIL_TYPE = "<< EmailServiceImpl :: determineMailType <<";

  private static final String SUBJECT = "Subject";

  private final JavaMailSender emailSender;

  @Value("${project.host-ip}")
  private String projectHostIp;

  @Value("${project.host-port}")
  private String projectPort;

  @Autowired
  public EmailServiceImpl(final JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  @Override
  public void createRegistrationMail(final Users user, final Enum emailType) {
    HashMap properties = determineMailType(user, emailType);
    MimeMessage mimeMessage = emailSender.createMimeMessage();

    LOGGER.info(">> EmailServiceImpl :: createRegistrationMail >>");
    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
      mimeMessageHelper.setFrom("no-reply@squareit.com");
      mimeMessageHelper.setTo(user.getEmail());
      mimeMessageHelper.setSubject((String) properties.get(SUBJECT));
      mimeMessageHelper.setText((String) properties.get("Body"), true);

      emailSender.send(mimeMessage);
      LOGGER.info("<< EmailServiceImpl :: createRegistrationMail <<");
    } catch (MessagingException e) {
      LOGGER.warn("-- EmailServiceImpl :: createRegistrationMail :: Messaging exception for user with email: "
          + user.getEmail() + ", caused by: " + e.getCause().getMessage() + " --");
    }
  }

  private HashMap determineMailType(final Users user, final Enum emailType) {
    LOGGER.info(">> EmailServiceImpl :: determineMailType >>");

    final HashMap<String, String> properties = new HashMap<>();
    final String token = user.getVerificationToken().getToken();
    final String firstName = user.getFirstName();
    final String email = user.getEmail();

    switch ((EmailTypes) emailType) {
      case NEW_ACCOUNT:
        LOGGER.info(DETERMINE_MAIL_TYPE_CASE, emailType);
        properties.put(SUBJECT, getNewAccountSubject());
        properties.put("Body", getNewAccountBody(firstName, email, token));
        LOGGER.info(DETERMINE_MAIL_TYPE);
        break;
      case NEW_ACCOUNT_RESEND:
        LOGGER.info(DETERMINE_MAIL_TYPE_CASE, emailType);
        properties.put(SUBJECT, getNewAccountResendSubject());
        properties.put("Body", getNewAccountBody(firstName, email, token));
        LOGGER.info(DETERMINE_MAIL_TYPE);
        break;
      case NEW_ACCOUNT_CONFIRMED:
        LOGGER.info(DETERMINE_MAIL_TYPE_CASE, emailType);
        properties.put(SUBJECT, getNewAccountConfirmationSubject());
        properties.put("Body", getNewAccountConfirmationBody(firstName));
        LOGGER.info(DETERMINE_MAIL_TYPE);
        break;
      default:
        break;
    }

    return properties;
  }

  private String getNewAccountUrl() {
    LOGGER.info(">> EmailServiceImpl :: getNewAccountUrl >>");
    final String newAccountUrl = projectHostIp  + ":" + projectPort + "/rest/user/v1/confirmRegistration/";
    LOGGER.info("<< EmailServiceImpl :: getNewAccountUrl :: url: {} <<", newAccountUrl);
    return newAccountUrl;
  }

  private String getResendNewAccountUrl() {
    LOGGER.info(">> EmailServiceImpl :: getResendNewAccountUrl >>");
    final String resendNewAccountUrl = projectHostIp + ":" + projectPort + "/rest/user/v1/resendRegistrationEmail/";
    LOGGER.info("<< EmailServiceImpl :: getResendNewAccountUrl :: url: {} <<", resendNewAccountUrl);
    return resendNewAccountUrl;
  }

  private String getNewAccountSubject() {
    LOGGER.info(">><< EmailServiceImpl :: getNewAccountSubject >><<");
    return "Account confirmation message from SquareIt";
  }

  private String getNewAccountResendSubject() {
    LOGGER.info(">><< EmailServiceImpl :: getNewAccountResendSubject >><<");
    return "New account confirmation message from SquareIt";
  }

  private String getNewAccountConfirmationSubject() {
    LOGGER.info(">><< EmailServiceImpl :: getNewAccountConfirmationSubject >><<");
    return "Welcome to SquareIt - Account confirmed!";
  }

  private String getNewAccountBody(final String firstName, final String email, final String token) {
    LOGGER.info(">><< EmailServiceImpl :: getNewAccountBody >><<");
    return "<!DOCTYPE html>"
        + "<html>"
        + "<head>"
          + "<meta charset='utf-8' name='viewport' content='width=device-width, initial-scale=1.0'/>"
        + "</head>"
        + "<body style='background-color: #FAFAFA;'>"
          + "<div style='height: 25px'></div>"
            + "<center>"
              + "<div style='background-color: black; color: #F0E68C; max-width: 500px; border: 1px solid black; "
              + "font-weight: bold;'>"
                + "<h1 style='background-color: #F0E68C; color: black; padding: 15px 0px 15px; margin: auto;'>"
                  + "SquareIt"
                + "</h1>"
                + "<br/>"
                + "<h1>Hello " + firstName + "</h1>"
                + "Click on the link below to verify your account."
                + "<br/><br/>"
                + "<form>"
                  + "<button style='background-color: black; border-color: #F0E68C; border-radius: 10px; "
                    + "font-weight: bold; color:#F0E68C; display:block; height: 30px; width: 200px;' "
                    + "formaction=" + getNewAccountUrl() + token + ">Verify account</button>"
                + "</form>"
                + "<br/><br/><br/>"
                + "Does the activation link not work?"
                + "<br/><br/>"
                + "<form>"
                  + "<button style='background-color: black; border-color: #F0E68C; border-radius: 10px; "
                    + "font-weight: bold; color:#F0E68C; display:block; height: 30px; width: 200px;' "
                    + "formaction=" + getResendNewAccountUrl() + email + ">Resend verification email</button>"
                + "</form>"
                + "<br/><br/><br/>"
                + "<footer style='padding: 5px; color: black; background-color: #F0E68C;'>"
                  + "Copyright &copy; SquareIt"
                + "</footer>"
              + "</div>"
            + "</center>"
          + "<div style='height: 25px'></div>"
        + "</body>"
        + "</html>";
  }

  private String getNewAccountConfirmationBody(final String firstName) {
    LOGGER.info(">><< EmailServiceImpl :: getNewAccountConfirmationBody >><<");
    return "<!DOCTYPE html>"
        + "<html>"
        + "<head>"
          + "<meta charset='utf-8' name='viewport' content='width=device-width, initial-scale=1.0'/>"
        + "</head>"
        + "<body style='background-color: #FAFAFA;'>"
          + "<div style='height: 25px'></div>"
          + "<center>"
            + "<div style='background-color: black; color: #F0E68C; max-width:500px; border: 1px solid black; "
              + "font-weight: bold;'>"
              + "<h1 style='background-color: #F0E68C; color: black; padding: 15px 0px 15px; margin: auto;'>"
                + "SquareIt"
              + "</h1>"
              + "<br/>"
              + "<h1>Good news, " + firstName + "</h1>"
              + "<br/>"
              + "Account confirmed."
              + "<br/><br/><br/><br/>"
              + "<footer style='padding: 5px; color: black; background-color: #F0E68C;'>"
                + "Copyright &copy; SquareIt"
              + "</footer>"
            + "</div>"
          + "</center>"
          + "<div style='height: 25px'></div>"
        + "</body>"
        + "</html>";
  }
}
