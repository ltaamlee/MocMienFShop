package mocmien.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mocmien.com.dto.response.users.UserResponse;
import mocmien.com.dto.response.users.UserStats;
import mocmien.com.entity.User;
import mocmien.com.enums.UserStatus;
import mocmien.com.service.UserService;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

	@Autowired
	private UserService userService;
	
	// -----------------------
    // Thống kê nhanh người dùng
    // -----------------------
    @GetMapping("/stats")
    public UserStats getUserStatistics() {
        return userService.getUserStatistics();
    }
	
    // -----------------------
    // Lấy danh sách user phân trang
    // -----------------------
    @GetMapping
    public Page<UserResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required=false) String role
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        return userService.findAll(keyword, status, isActive, role, pageable);
    }
    
    @PatchMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable Integer userId) {
        userService.changeBlock(userId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        userService.deleteById(userId);
        System.out.println("[ACTION LOG] User " + userId + " đã bị xóa");
        return ResponseEntity.ok("User đã bị xóa thành công");
    }


}
