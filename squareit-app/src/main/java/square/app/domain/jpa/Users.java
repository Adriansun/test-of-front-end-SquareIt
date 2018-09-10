package square.app.domain.jpa;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import square.api.domain.constants.Constraint;
import square.api.domain.constants.RoleTypes;

@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "Users.countAllActiveUsers", query = "SELECT COUNT(u) FROM Users u WHERE u.userDeleted = FALSE "
        + "AND u.enabled = TRUE"),
    @NamedQuery(name = "Users.findUserByToken", query = "SELECT u FROM Users u LEFT JOIN FETCH u.verificationToken "
        + "WHERE u.userDeleted = FALSE AND u.verificationToken.token = :token")
})
public class Users implements Serializable {

  private static final long serialVersionUID = 2323232323L;

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = Constraint.MAX_USERNAME)
  private String userName;

  @Column(nullable = false, length = Constraint.MAX_FIRSTNAME)
  private String firstName;

  @Column(nullable = false, length = Constraint.MAX_LASTNAME)
  private String lastName;

  @Column(nullable = false, length = Constraint.MAX_EMAIL)
  private String email;

  @Column(nullable = false, length = Constraint.MAX_PASSWORD_DB)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = Constraint.MAX_ROLETYPE)
  private RoleTypes role;

  @Column(nullable = false)
  private boolean enabled;

  @Column(nullable = false)
  private ZonedDateTime createdDate;

  @Column
  private ZonedDateTime updatedDate;

  @Column(nullable = false)
  private boolean userDeleted;

  @JoinColumn(name = "fk_verificationToken", nullable = false)
  @OneToOne(cascade = CascadeType.ALL)
  private VerificationToken verificationToken;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<Number> number;

  @Override
  public String toString() {
    return "Users{"
        + "id=" + id
        + ", userName='" + userName + '\''
        + ", firstName='" + firstName + '\''
        + ", lastName='" + lastName + '\''
        + ", email='" + email + '\''
        + ", role=" + role
        + ", enabled=" + enabled
        + ", createdDate=" + createdDate
        + ", updatedDate=" + updatedDate
        + ", userDeleted=" + userDeleted
        + '}';
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(final String userName) {
    this.userName = userName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public RoleTypes getRole() {
    return role;
  }

  public void setRole(final RoleTypes role) {
    this.role = role;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public ZonedDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(final ZonedDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public ZonedDateTime getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(final ZonedDateTime updatedDate) {
    this.updatedDate = updatedDate;
  }

  public VerificationToken getVerificationToken() {
    return verificationToken;
  }

  public void setVerificationToken(final VerificationToken verificationToken) {
    this.verificationToken = verificationToken;
  }

  public List<Number> getNumber() {
    return number;
  }

  public void setNumber(final List<Number> number) {
    this.number = number;
  }

  public boolean getUserDeleted() {
    return userDeleted;
  }

  public void setUserDeleted(final boolean userDeleted) {
    this.userDeleted = userDeleted;
  }
}
