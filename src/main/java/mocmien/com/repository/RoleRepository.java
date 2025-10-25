package mocmien.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.Role;
import mocmien.com.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	List<Role> findAll();

	// Tìm role theo tên RoleName (enum)
	Optional<Role> findByRoleName(RoleName roleName);

	// Tùy chọn: kiểm tra tồn tại theo RoleName
	boolean existsByRoleName(RoleName roleName);

}
