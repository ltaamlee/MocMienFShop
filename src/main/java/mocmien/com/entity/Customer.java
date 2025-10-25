package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.Rank;

@Entity
@Table(name = "Customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level", nullable = false)
    private Level level;

    @Column(name = "fullName", nullable = false, columnDefinition = "nvarchar(500)" )
    private String fullName;

    @Column(name = "idCard", unique = true, columnDefinition = "varchar(20)" )
    private String idCard;

    @Column(name = "dob", nullable = true)
    private LocalDate dob;

    @Column(name = "point", nullable = false, columnDefinition = "int default 0")
    private Integer point = 0;
    
    @Column(name = "eWallet", nullable = false, precision = 15, scale = 2)
    private BigDecimal eWallet = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerAddress> addresses;
}