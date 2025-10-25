package mocmien.com.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocmien.com.enums.StaffPosition;

@Entity
@Table(name = "Staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Liên kết tới User (tài khoản)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // Liên kết tới Store
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;
    
    @Column(name = "idCard", unique = true, columnDefinition = "varchar(20)" )
    private String idCard;

    @Column(name = "position", columnDefinition = "nvarchar(100)")
    private StaffPosition position; // Chức vụ (ví dụ: nhân viên bán hàng, quản lý kho)

    @Column(name = "hireDate")
    private LocalDate hireDate; // Ngày vào làm
    
    @Column(name = "leaveDate")
    private LocalDate leaveDate; // Ngày nghỉ việc
    
    @Column(name = "salary", columnDefinition = "decimal(18,2)")
    private BigDecimal salary = BigDecimal.ZERO;
       
    @Column(name = "isActive", nullable = false)
    private boolean isActive = true; // Trạng thái hoạt động

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