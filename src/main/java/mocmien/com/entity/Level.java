package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.Rank;

@Entity
@Table(name = "Level")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Level {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "nvarchar(100)" )
    private Rank name; // BASIC, BRONZE, SILVER, GOLD, PLATINUM, VIP

    @Column(name = "minPoint", nullable = false, columnDefinition = "int default 0")
    private Integer minPoint = 0;

    @Column(name = "discount", columnDefinition = "DECIMAL(18,2)")
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
