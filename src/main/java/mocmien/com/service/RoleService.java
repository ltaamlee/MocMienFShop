package mocmien.com.service;

import java.util.List;

import mocmien.com.entity.Role;

public interface RoleService {

	List<Role> getAllRoles();

    Role getRoleById(Integer id);

    Role saveRole(Role role);

    void deleteRole(Integer id);

    Role getRoleByName(String roleName);
}
