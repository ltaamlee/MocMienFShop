package mocmien.com.entity;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// Khóa ngoại tới bảng User
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
	private User user;

	// Khóa ngoại tới bảng Product
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productId", referencedColumnName = "id", nullable = false)
	private Product product;

	@Column(name = "comment", columnDefinition = "nvarchar(255)")
	private String comment;

	@Column(name = "rating", columnDefinition = "varchar(MAX)")
	private Integer rating; // giá trị từ 1 đến 5

	@Column(name = "createAt")
	private LocalDateTime createAt;

	@PrePersist
	public void prePersist() {
		this.createAt = LocalDateTime.now();
	}

	// ==============================
	// Getters & Setters
	// ==============================

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}
}