package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.Rank;

@Entity
@Table(name = "Store")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Hạng cửa hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level", nullable = false)
    private Level level;

    // Chủ cửa hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendorId", nullable = false)
    private User vendor;

    // Danh sách nhân viên
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Staff> staff; // staff là các user có vai trò nhân viên

    @Column(name = "storeName", nullable = false, unique = true, columnDefinition = "nvarchar(500)")
    private String storeName;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Staff> staffList;

    @Column(name = "avatar", columnDefinition = "varchar(MAX)")
    private String avatar;

    @Column(name = "cover", columnDefinition = "varchar(MAX)")
    private String cover;

    // Danh sách ảnh nổi bật
    @ElementCollection
    @Column(name = "featureImages")
    private List<String> featureImages;

    @Column(name = "point", nullable = false, columnDefinition = "int default 0")
    private Integer point = 0;

    @Column(name = "eWallet", nullable = false, precision = 15, scale = 2)
    private BigDecimal eWallet = BigDecimal.ZERO;

    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

	@Column(name="isActive", nullable = false, columnDefinition="BIT DEFAULT 1")
	private boolean isActive = true; 

    @Column(name = "isOpen", nullable = false)
    private boolean isOpen = false;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    // ==============================
    // Callback tự động cập nhật ngày
    // ==============================
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