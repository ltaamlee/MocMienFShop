package mocmien.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import mocmien.com.security.CustomUserDetailsService;
import mocmien.com.security.jwt.JwtAuthenticationEntryPoint;
import mocmien.com.security.jwt.JwtAuthenticationFilter;
import mocmien.com.security.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig {
		
	private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenProvider tokenProvider;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtTokenProvider tokenProvider) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.tokenProvider = tokenProvider;
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, customUserDetailsService);
    }

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	
                .requestMatchers("/", "/web/**", "/api/**", "/api/chat-ai","/api/auth/register", "/api/auth/login/", 
                                 "/index", "/home", "/register", "/login", "/logout", "/product/**", 
                                 "/about", "/contact", "/error", "/styles/**", "/css/**", "/js/**", 
                                 "/images/**", "/image/**", "/webjars/**", "/forgot-password/**", 
                                 "/verify-otp/**", "/api/payment/momo/callback")
                .permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 🔹 Stateless session cho JWT
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .logout(logout -> logout.disable());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}