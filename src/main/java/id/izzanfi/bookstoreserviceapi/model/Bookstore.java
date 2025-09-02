package id.izzanfi.bookstoreserviceapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "bookstores", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name"),
    @UniqueConstraint(columnNames = "address")
})
public class Bookstore extends AuditTrail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long bookstoreId;

  @Column(name = "name", nullable = false)
  private String bookstoreName;

  @Column(name = "address", nullable = false)
  private String bookstoreAddress;

  @Column(name = "phone_no")
  private String bookstorePhoneNo;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @OneToMany(mappedBy = "bookstore")
  private Set<BookStock> bookStocks = new HashSet<>();
}
