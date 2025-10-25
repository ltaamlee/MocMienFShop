package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import mocmien.com.entity.Role;
import mocmien.com.enums.RoleName;

public interface RoleService {
	
	List<Role> findAll();

	// Tìm role theo tên RoleName (enum)
	Optional<Role> findByRoleName(RoleName roleName);

	// Tùy chọn: kiểm tra tồn tại theo RoleName
	boolean existsByRoleName(RoleName roleName);

}
