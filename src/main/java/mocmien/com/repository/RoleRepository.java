package mocmien.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mocmien.com.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	Role findByRoleName(String roleName);
}
