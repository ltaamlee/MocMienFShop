package mocmien.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "Addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressID")
    private Integer addressId;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer; // Liên kết với Customer

    @Column(name = "ReceiverName", length = 100, nullable = false)
    private String receiverName; // Tên người nhận

    @Column(name = "Phone", length = 15, nullable = false)
    private String phone; // Số điện thoại nhận hàng

    @Column(name = "Street", length = 255, nullable = false)
    private String street; // Đường, số nhà

    @Column(name = "City", length = 100, nullable = false)
    private String city; // Thành phố / quận / huyện

    @Column(name = "District", length = 100)
    private String district; // Quận / huyện

    @Column(name = "Ward", length = 100)
    private String ward; // Phường / xã

    @Column(name = "IsDefault")
    private Boolean isDefault = false; // Địa chỉ mặc định

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
