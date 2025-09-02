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
@Table(name = "books", uniqueConstraints = {
    @UniqueConstraint(columnNames = "title"),
    @UniqueConstraint(columnNames = "isbn")
})
public class Book extends AuditTrail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long bookId;

  @Column(name = "isbn", nullable = false)
  private String bookIsbn;

  @Column(name = "title", nullable = false)
  private String bookTitle;

  @Column(name = "author", nullable = false)
  private String bookAuthor;

  @Column(name = "genre")
  private String bookGenre;

  @OneToMany(mappedBy = "book")
  private Set<BookStock> bookStocks = new HashSet<>();
}
