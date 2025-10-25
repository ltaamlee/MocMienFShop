package mocmien.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mocmien.com.entity.Role;
import mocmien.com.enums.RoleName;
import mocmien.com.repository.RoleRepository;
import mocmien.com.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService{

	@Autowired
	private RoleRepository role;
	
	@Override
	public List<Role> findAll() {
		return role.findAll();
	}

	@Override
	public Optional<Role> findByRoleName(RoleName roleName) {
		return role.findByRoleName(roleName);
	}

	@Override
	public boolean existsByRoleName(RoleName roleName) {
		return role.existsByRoleName(roleName);
	}

}
