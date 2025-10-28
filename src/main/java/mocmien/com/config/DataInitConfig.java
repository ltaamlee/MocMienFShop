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
                createOrUpdateUser(userService, roleRepository, userProfileRepository, passwordEncoder,
                        "admin@example.com", "admin", "admin123", "Quản trị viên", "0123456789", RoleName.ADMIN);

                // 3. Tạo Vendor
                createOrUpdateUser(userService, roleRepository, userProfileRepository, passwordEncoder,
                        "vendor@example.com", "vendor", "123", "Phương Thi", "0987654321", RoleName.VENDOR);

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
        UserProfile userPro = userProfileRepository.findByUser(user)
                .orElse(new UserProfile());
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
    
    
//    @Bean
//    CommandLineRunner initStores(StoreRepository storeRepository, UserRepository userRepository, LevelRepository levelRepository) {
//        return args -> {
//            // Lấy vendor
//            User vendor = userRepository.findById(2).orElse(null);
//            if (vendor == null) {
//                System.out.println("Vendor with ID=2 not found!");
//                return;
//            }
//
//            // Lấy Level mặc định
//            Level defaultLevel = levelRepository.findById(1).orElse(null);
//
//            // Tạo store mẫu
//            Store store1 = new Store();
//            store1.setStoreName("Cửa hàng Hoa Mai");
//            store1.setVendor(vendor);
//            store1.setLevel(defaultLevel);
//            store1.setActive(true);
//            store1.setOpen(true);
//            store1.setPoint(100);
//            store1.seteWallet(BigDecimal.valueOf(5000));
//            store1.setRating(BigDecimal.valueOf(4.5));
//            store1.setAvatar(null);
//            store1.setCover(null);
//            store1.setFeatureImages(List.of());
//
//            Store store2 = new Store();
//            store2.setStoreName("Cửa hàng Bách Hóa Xanh");
//            store2.setVendor(vendor);
//            store2.setLevel(defaultLevel);
//            store2.setActive(true);
//            store2.setOpen(false);
//            store2.setPoint(80);
//            store2.seteWallet(BigDecimal.valueOf(3000));
//            store2.setRating(BigDecimal.valueOf(4.0));
//            store2.setAvatar(null);
//            store2.setCover(null);
//            store2.setFeatureImages(List.of());
//
//            // Lưu vào database
//            storeRepository.saveAll(List.of(store1, store2));
//
//            System.out.println("Sample stores created!");
//        };
//    }
}
