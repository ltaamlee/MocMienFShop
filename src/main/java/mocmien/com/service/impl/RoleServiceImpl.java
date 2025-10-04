package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mocmien.com.entity.Role;
import mocmien.com.repository.RoleRepository;
import mocmien.com.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Role getRoleById(Integer id) {
		Optional<Role> role = roleRepository.findById(id);
		return role.orElse(null);
	}

	@Override
	public Role saveRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public void deleteRole(Integer id) {
		roleRepository.deleteById(id);
	}

	@Override
	public Role getRoleByName(String roleName) {
		return roleRepository.findByRoleName(roleName);
	}
}
