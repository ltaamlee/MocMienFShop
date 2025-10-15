package mocmien.com.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 100, message = "Tên đăng nhập phải từ 3-100 ký tự")
    private String username;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không quá 100 ký tự")
    private String email;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 255, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
    
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không quá 100 ký tự")
    private String hoTen;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String sdt;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	
	public RegisterRequest() {
		super();
	}

	public RegisterRequest(
			@NotBlank(message = "Tên đăng nhập không được để trống") @Size(min = 3, max = 100, message = "Tên đăng nhập phải từ 3-100 ký tự") String username,
			@NotBlank(message = "Email không được để trống") @Email(message = "Email không hợp lệ") @Size(max = 100, message = "Email không quá 100 ký tự") String email,
			@NotBlank(message = "Mật khẩu không được để trống") @Size(min = 6, max = 255, message = "Mật khẩu phải có ít nhất 6 ký tự") String password,
			@NotBlank(message = "Họ tên không được để trống") @Size(max = 100, message = "Họ tên không quá 100 ký tự") String hoTen,
			@Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số") String sdt) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.hoTen = hoTen;
		this.sdt = sdt;
	}
    
    
}
