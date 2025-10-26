package mocmien.com.entity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Flower")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flower {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
	private String name; // Tên loại hoa

	@Column(name = "color", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
	private String color; // Màu sắc

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true; // Mặc định còn hoạt động

	@OneToMany(mappedBy = "flower", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductFlower> productFlowers;
	
	@Column(name = "createAt", nullable = false)
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