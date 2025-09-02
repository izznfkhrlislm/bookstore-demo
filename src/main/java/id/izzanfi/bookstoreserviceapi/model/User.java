package id.izzanfi.bookstoreserviceapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name"),
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "phone_number")
})
public class User extends AuditTrail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long userId;

  @Column(name = "name", nullable = false)
  private String userName;

  @Column(name = "email", nullable = false)
  private String userEmail;

  @Column(name = "password", nullable = false)
  private String userPassword;

  @Column(name = "address")
  private String userAddress;

  @Column(name = "phone_number")
  private String userPhoneNumber;

  @Column(name = "user_bio")
  private String userBio;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_books",
      joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
  private Set<Book> userBooks = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  // Custom Constructor
  public User(String userName, String userEmail, String userPassword) {
    this.userName = userName;
    this.userEmail = userEmail;
    this.userPassword = userPassword;
  }
}
