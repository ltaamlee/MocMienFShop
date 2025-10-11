package mocmien.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Sellers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seller {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SellerID")
	private Integer sellerId;

	@OneToOne
	@JoinColumn(name = "UserID", nullable = false, unique = true)
	private User user; 

	@Column(name = "ShopName", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
	private String shopName;

	@Column(name = "ShopAddress", length = 255, columnDefinition = "nvarchar(255)")
	private String shopAddress;

	@Column(name = "ShopPhone", length = 15)
	private String shopPhone; 
	
	@Column(name = "Status")
	private Integer status = 1; // 1: active, 0: inactive, -1: blocked

	@Column(name = "Rating")
	private Double rating = 0.0;

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
