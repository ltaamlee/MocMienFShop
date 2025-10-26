package mocmien.com.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import mocmien.com.entity.Level;
import mocmien.com.entity.Role;
import mocmien.com.entity.User;
import mocmien.com.enums.Rank;
import mocmien.com.enums.RoleName;
import mocmien.com.repository.LevelRepository;
import mocmien.com.repository.RoleRepository;
import mocmien.com.service.UserService;

@Configuration
public class DataInitConfig {

    @Bean
    CommandLineRunner initRolesAndUsers(UserService userService,
                                        RoleRepository roleRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // 1. Tạo tất cả Role nếu chưa tồn tại
                for (RoleName roleName : RoleName.values()) {
                    roleRepository.findByRoleName(roleName).orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName(roleName);
                        Role savedRole = roleRepository.save(role);
                        System.out.println("✓ Role " + roleName + " được tạo!");
                        return savedRole;
                    });
                }

                // 2. Tạo Admin
                createOrUpdateUser(userService, roleRepository, passwordEncoder,
                        "admin@example.com", "admin", "admin123", "0123456789", RoleName.ADMIN);

                // 3. Tạo Vendor
                createOrUpdateUser(userService, roleRepository, passwordEncoder,
                        "vendor@example.com", "vendor", "123", "0987654321", RoleName.VENDOR);

            } catch (Exception e) {
                System.err.println("✗ Lỗi khi tạo hoặc cập nhật user: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    private void createOrUpdateUser(UserService userService,
                                    RoleRepository roleRepository,
                                    PasswordEncoder passwordEncoder,
                                    String email,
                                    String username,
                                    String rawPassword,
                                    String phone,
                                    RoleName roleName) {

        User user = userService.findByEmail(email).orElseGet(User::new);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone(phone);
        user.setActive(true);
        user.setRole(roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " không tồn tại!")));
        userService.save(user);

        System.out.println("✓ User " + username + " đã được tạo hoặc cập nhật với role " + roleName);
    }

    @Bean
    CommandLineRunner initLevels(LevelRepository levelRepository) {
        return args -> {
            for (Rank rank : Rank.values()) {
                levelRepository.findByName(rank).ifPresentOrElse(
                        existing -> System.out.println("Level " + rank.name() + " đã tồn tại, bỏ qua."),
                        () -> {
                            Level level = new Level();
                            level.setName(rank);
                            level.setMinPoint(0);
                            level.setDiscount(BigDecimal.ZERO);
                            level.setCreateAt(LocalDateTime.now());
                            level.setUpdateAt(LocalDateTime.now());
                            levelRepository.save(level);
                            System.out.println("✓ Level mặc định " + rank.name() + " đã được tạo.");
                        }
                );
            }
            System.out.println("Init levels hoàn tất.");
        };
    }
}
