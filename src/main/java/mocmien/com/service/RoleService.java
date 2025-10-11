package mocmien.com.service;

import java.util.List;
import java.util.Optional;

import mocmien.com.entity.Role;
import mocmien.com.enums.RoleName;

public interface RoleService {

	List<Role> findAll();

    Optional<Role> findById(Integer id);

    Optional<Role> findByRoleName(RoleName roleName);

    boolean existsByRoleName(RoleName roleName);

    Role save(Role role);

    void deleteById(Integer id);
}
