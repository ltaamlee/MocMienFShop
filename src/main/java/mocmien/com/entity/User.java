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
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UserID")
	private Integer userId;

	@Column(name = "Username", nullable = false, unique = true, length = 100)
	private String username;
	
	@Column(name = "FullName", nullable = false, length = 100)
	private String fullName;

	@Column(name = "Email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "PasswordHash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "Phone", length = 15)
	private String phone;

	@Column(name = "Address", length = 255)
	private String address;

	@ManyToOne
	@JoinColumn(name = "RoleID")
	private Role role;

	@Column(name = "Status")
	private Integer status = 1; // 1: Active, 0: Inactive, -1: Blocked

	@Column(name = "Code", length = 50)
	private String code;

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

	
	//Getter và Setter
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public User() {
		super();
	}

	
	public User(Integer userId, String username, String passwordHash) {
		super();
		this.userId = userId;
		this.username = username;
		this.passwordHash = passwordHash;
	}

		
	public User(String username, String passwordHash) {
		super();
		this.username = username;
		this.passwordHash = passwordHash;
	}

	public User(Integer userId, String username, String fullName, String email, String passwordHash, String phone,
			String address, Role role, Integer status, String code, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.userId = userId;
		this.username = username;
		this.fullName = fullName;
		this.email = email;
		this.passwordHash = passwordHash;
		this.phone = phone;
		this.address = address;
		this.role = role;
		this.status = status;
		this.code = code;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

}
