package mocmien.com.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import mocmien.com.entity.Level;
import mocmien.com.entity.Role;
import mocmien.com.entity.Store;
import mocmien.com.entity.User;
import mocmien.com.entity.UserProfile;
import mocmien.com.enums.Rank;
import mocmien.com.enums.RoleName;
import mocmien.com.repository.LevelRepository;
import mocmien.com.repository.RoleRepository;
import mocmien.com.repository.UserProfileRepository;
import mocmien.com.repository.UserRepository;
import mocmien.com.repository.StoreRepository;
import mocmien.com.service.UserService;

@Configuration
public class DataInitConfig {

    @Bean
    CommandLineRunner initRolesAndUsers(UserService userService,
                                        RoleRepository roleRepository,
                                        UserProfileRepository userProfileRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // 1️⃣ Tạo tất cả Role nếu chưa tồn tại
                for (RoleName roleName : RoleName.values()) {
                    roleRepository.findByRoleName(roleName).orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName(roleName);
                        Role savedRole = roleRepository.save(role);
                        System.out.println("✓ Role " + roleName + " được tạo!");
                        return savedRole;
                    });
                }

                // 2️⃣ Tạo Admin
                createOrUpdateUser(userService, roleRepository, userProfileRepository, passwordEncoder,
                        "admin@example.com", "admin", "admin123", "Quản trị viên", "0123456789", RoleName.ADMIN);

                // 3️⃣ Tạo 3 Vendor
                createOrUpdateUser(userService, roleRepository, userProfileRepository, passwordEncoder,
                        "vendor1@mocmien.com", "vendor1", "123456", "Nguyễn Văn A", "0981000001", RoleName.VENDOR);

                createOrUpdateUser(userService, roleRepository, userProfileRepository, passwordEncoder,
                        "vendor2@mocmien.com", "vendor2", "123456", "Trần Thị B", "0981000002", RoleName.VENDOR);

                createOrUpdateUser(userService, roleRepository, userProfileRepository, passwordEncoder,
                        "vendor3@mocmien.com", "vendor3", "123456", "Lê Văn C", "0981000003", RoleName.VENDOR);

            } catch (Exception e) {
                System.err.println("✗ Lỗi khi tạo hoặc cập nhật user: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    private void createOrUpdateUser(UserService userService,
                                    RoleRepository roleRepository,
                                    UserProfileRepository userProfileRepository,
                                    PasswordEncoder passwordEncoder,
                                    String email,
                                    String username,
                                    String rawPassword,
                                    String fullName,
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

        // Kiểm tra xem user đã có profile chưa
        UserProfile userPro = userProfileRepository.findByUser(user).orElse(new UserProfile());
        userPro.setFullName(fullName);
        userPro.setUser(user); // Gắn user vào profile
        userProfileRepository.save(userPro);

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

    // 4️⃣ Tạo 3 cửa hàng cho 3 Vendor
    @Bean
    CommandLineRunner initStores(StoreRepository storeRepository, 
                                 UserRepository userRepository, 
                                 LevelRepository levelRepository) {
        return args -> {
            Level defaultLevel = levelRepository.findAll().stream().findFirst().orElse(null);
            if (defaultLevel == null) {
                System.out.println("⚠ Không tìm thấy Level mặc định. Bỏ qua tạo Store.");
                return;
            }

            // Vendor 1
            userRepository.findByEmail("vendor1@mocmien.com").ifPresent(vendor -> {
                if (storeRepository.findByVendor(vendor).isEmpty()) {
                    Store store = new Store();
                    store.setStoreName("Cửa hàng Mộc Miên");
                    store.setVendor(vendor);
                    store.setLevel(defaultLevel);
                    store.setActive(true);
                    store.setOpen(true);
                    store.setPoint(150);
                    store.seteWallet(BigDecimal.valueOf(5000));
                    store.setRating(BigDecimal.valueOf(4.7));
                    store.setAvatar(null);
                    store.setCover(null);
                    store.setFeatureImages(List.of());
                    storeRepository.save(store);
                    System.out.println("✓ Store của vendor1 được tạo!");
                }
            });

            // Vendor 2
            userRepository.findByEmail("vendor2@mocmien.com").ifPresent(vendor -> {
                if (storeRepository.findByVendor(vendor).isEmpty()) {
                    Store store = new Store();
                    store.setStoreName("Cửa hàng Hương Quê");
                    store.setVendor(vendor);
                    store.setLevel(defaultLevel);
                    store.setActive(true);
                    store.setOpen(false);
                    store.setPoint(100);
                    store.seteWallet(BigDecimal.valueOf(3000));
                    store.setRating(BigDecimal.valueOf(4.3));
                    store.setAvatar(null);
                    store.setCover(null);
                    store.setFeatureImages(List.of());
                    storeRepository.save(store);
                    System.out.println("✓ Store của vendor2 được tạo!");
                }
            });

            // Vendor 3
            userRepository.findByEmail("vendor3@mocmien.com").ifPresent(vendor -> {
                if (storeRepository.findByVendor(vendor).isEmpty()) {
                    Store store = new Store();
                    store.setStoreName("Cửa hàng Tươi Sạch");
                    store.setVendor(vendor);
                    store.setLevel(defaultLevel);
                    store.setActive(true);
                    store.setOpen(true);
                    store.setPoint(120);
                    store.seteWallet(BigDecimal.valueOf(4000));
                    store.setRating(BigDecimal.valueOf(4.6));
                    store.setAvatar(null);
                    store.setCover(null);
                    store.setFeatureImages(List.of());
                    storeRepository.save(store);
                    System.out.println("✓ Store của vendor3 được tạo!");
                }
            });
        };
    }
}
