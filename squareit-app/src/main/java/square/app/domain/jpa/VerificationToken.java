package square.app.domain.jpa;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import square.app.constants.TimeConstants;

@Entity
@Table(name = "token")
public class VerificationToken implements Serializable {

  private static final long serialVersionUID = 3434343434L;

  private static final int EXPIRATION = 60 * 24;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Column(nullable = false, length = 36)
  private String token;

  @Column(nullable = false)
  private ZonedDateTime expiryDate;

  @Column
  private ZonedDateTime refreshToken;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "verificationToken")
  private Users user;

  public VerificationToken() {
    super();
  }

  /**
   * Verification token.
   *
   * @param token token
   */
  public VerificationToken(final String token) {
    super();

    this.token = token;
    this.expiryDate = calculateExpiryDate(EXPIRATION);
  }

  /**
   * Create create verification token, set expiration date, and current time token for user.
   *
   * @param user  user
   * @param token token
   */
  public VerificationToken(final Users user, final String token) {
    super();

    this.user = user;
    this.token = token;
    this.expiryDate = calculateExpiryDate(EXPIRATION);
    this.refreshToken = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC));
  }

  /**
   * Update verification token, update expiration date, and update current refresh token
   * .
   *
   * @param token token
   */
  public void updateToken(final String token) {
    this.token = token;
    this.expiryDate = calculateExpiryDate(EXPIRATION);
    this.refreshToken = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC));
  }

  public void updateRefreshToken(final String token) {
    this.token = token;
    this.refreshToken = ZonedDateTime.now(ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC));
  }

  private ZonedDateTime calculateExpiryDate(final int expiryTimeInMinutes) {
    return ZonedDateTime.ofInstant(Instant.now(), ZoneId.of(TimeConstants.SERVER_TIMEZONE_UTC))
        .plusMinutes(expiryTimeInMinutes);
  }

  @Override
  public String toString() {
    return "VerificationToken{"
        + "id=" + id
        + ", token=\'" + token + '\''
        + ", expiryDate=" + expiryDate
        + ", refreshToken=" + refreshToken
        + '}';
  }

  public static int getExpiration() {
    return EXPIRATION;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Users getUser() {
    return user;
  }

  public void setUser(final Users user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  public ZonedDateTime getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(final ZonedDateTime expiryDate) {
    this.expiryDate = expiryDate;
  }

  public ZonedDateTime getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(final ZonedDateTime refreshToken) {
    this.refreshToken = refreshToken;
  }
}
