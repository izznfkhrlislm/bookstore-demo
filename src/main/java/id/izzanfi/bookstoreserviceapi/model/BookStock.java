package id.izzanfi.bookstoreserviceapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_stocks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"book_id", "bookstore_id"})
})
public class BookStock extends AuditTrail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long bookStockId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bookstore_id")
  private Bookstore bookstore;

  @Column(name = "stock", nullable = false)
  private int bookStock = 0;
}
