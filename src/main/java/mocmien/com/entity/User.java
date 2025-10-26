package mocmien.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import mocmien.com.enums.UserStatus;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userId")
	private Integer userId;

	@Column(name = "username", nullable = false, unique = true, columnDefinition = "nvarchar(100)" )
	private String username;
	
	@Column(name = "password", nullable = false, columnDefinition = "varchar(255)")
	private String password;
	
	@Column(name = "avatar", columnDefinition = "varchar(MAX)")
    private String avatar;

	@Column(name = "email", nullable = false, unique = true, columnDefinition = "varchar(100)")
	private String email;
	
	@Column(name = "isEmailActive", columnDefinition="BIT DEFAULT 1")
	private boolean isEmailActive = false;

	@Column(name = "phone", nullable = false, unique = true, columnDefinition = "varchar(20)")
	private String phone;
	
	@Column(name = "isPhoneActive", columnDefinition="BIT DEFAULT 1")
	private boolean isPhoneActive = false;
	
    @Enumerated(EnumType.STRING)
	@Column(name = "status")
	private UserStatus status = UserStatus.OFFLINE;
	
	@Column(name="isActive", nullable = false, columnDefinition="BIT DEFAULT 1")
	private boolean isActive = true; 
	
	@ManyToOne
	@JoinColumn(name = "role")
	private Role role;
	
	@ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;

	@Column(name = "code", columnDefinition = "varchar(20)")
	private String code;

	@Column(name = "createdAt")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt")
	private LocalDateTime updatedAt;
	
	@Column(name = "lastLoginAt")
	private LocalDateTime lastLoginAt;

	@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailActive() {
		return isEmailActive;
	}

	public void setEmailActive(boolean isEmailActive) {
		this.isEmailActive = isEmailActive;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isPhoneActive() {
		return isPhoneActive;
	}

	public void setPhoneActive(boolean isPhoneActive) {
		this.isPhoneActive = isPhoneActive;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
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

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(LocalDateTime lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
    
}
