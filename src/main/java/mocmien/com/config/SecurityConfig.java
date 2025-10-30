package mocmien.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import mocmien.com.controller.auth.OAuth2LoginSuccessHandler;
import mocmien.com.security.CustomUserDetailsService;
import mocmien.com.security.jwt.JwtAuthenticationEntryPoint;
import mocmien.com.security.jwt.JwtAuthenticationFilter;
import mocmien.com.security.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtTokenProvider tokenProvider;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler; // <-- thêm

	public SecurityConfig(CustomUserDetailsService customUserDetailsService,
			JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtTokenProvider tokenProvider,
			OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) { // <-- thêm
		this.customUserDetailsService = customUserDetailsService;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.tokenProvider = tokenProvider;
		this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler; // <-- gán
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(tokenProvider, customUserDetailsService);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				// Các endpoint public
				.requestMatchers("/", "/web/**", "/api/**", "/api/chat-ai", "/api/auth/register", "/api/auth/login/",
						"/index", "/home", "/register", "/login", "/logout", "/product/**", "/about", "/contact",
						"/error", "/api/favorite-products/**", "/favorites", "/styles/**", "/css/**", "/js/**",
						"/images/**", "/image/**", "/webjars/**", "/forgot-password/**", "/verify-otp/**",
						"/reset-password/**", "/api/payment/momo/callback", "/oauth2/**", "/shipper/**"// OAuth2
																										// endpoints
				).permitAll()
				// Chỉ admin mới truy cập /admin/**
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/vendor/**").hasRole("VENDOR")
				.requestMatchers("/shipper/**").hasRole("SHIPPER")
				.anyRequest().authenticated())
				// OAuth2 login
				.oauth2Login(oauth2 -> oauth2.loginPage("/login").successHandler(oAuth2LoginSuccessHandler) // dùng
																											// handler
																											// trên
						.failureUrl("/login?error=true"))
				// Exception handling
				.exceptionHandling(ex -> ex.accessDeniedHandler((req, res, ex2) -> res.sendRedirect("/403")))
				// Stateless session cho JWT
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.logout(logout -> logout.disable());

		// Thêm JWT filter trước UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}