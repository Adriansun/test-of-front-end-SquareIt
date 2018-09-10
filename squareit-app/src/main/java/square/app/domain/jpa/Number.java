package square.app.domain.jpa;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "number")
@NamedQueries({
    @NamedQuery(name = "Number.findOneNumberByIdAndUser", query = "SELECT n FROM Number n LEFT JOIN FETCH n.user "
        + "WHERE n.user = :user AND n.deletedNumber = FALSE AND n.id = :id"),
    @NamedQuery(name = "Number.countUserNumbers", query = "SELECT COUNT(n) FROM Number n WHERE n.user = :user "
        + "AND n.deletedNumber = FALSE"),
    @NamedQuery(name = "Number.findAllNumbers", query = "SELECT n FROM Number n WHERE n.user = :user ORDER BY n.id ASC")
})
public class Number implements Serializable {

  private static final long serialVersionUID = 1212121212L;

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userIdUnique;

  @Column(nullable = false)
  private Long number;

  @Column(nullable = false)
  private boolean deletedNumber;

  @ManyToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "user", nullable = false)
  private Users user;

  @Override
  public String toString() {
    return "Number{"
        + "id=" + id
        + ", userIdUnique=" + userIdUnique
        + ", number=" + number
        + ", deletedNumber=" + deletedNumber
        + '}';
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getUserIdUnique() {
    return userIdUnique;
  }

  public void setUserIdUnique(final Long userIdUnique) {
    this.userIdUnique = userIdUnique;
  }

  public Users getUser() {
    return user;
  }

  public void setUser(final Users user) {
    this.user = user;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(final Long number) {
    this.number = number;
  }

  public boolean getDeletedNumber() {
    return deletedNumber;
  }

  public void setDeletedNumber(final boolean deletedNumber) {
    this.deletedNumber = deletedNumber;
  }
}
