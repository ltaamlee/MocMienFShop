package mocmien.com.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mocmien.com.security.CustomUserDetailsService;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
		this.tokenProvider = tokenProvider;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (path.startsWith("/images/") || path.startsWith("/styles/") || path.startsWith("/css/")
				|| path.startsWith("/js/") || path.startsWith("/webjars/")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		if (path.equals("/logout")) {
	        filterChain.doFilter(request, response);
	        return;
	    }
		String token = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("JWT_TOKEN".equals(c.getName())) {
					token = c.getValue();
					break;
				}
			}
		}

		if (token != null && tokenProvider.validateToken(token)
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				String username = tokenProvider.getUsernameFromToken(token);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(auth);

			} catch (UsernameNotFoundException e) {
				Cookie expiredCookie = new Cookie("JWT_TOKEN", null);
				expiredCookie.setHttpOnly(true);
				expiredCookie.setSecure(true);
				expiredCookie.setPath("/");
				expiredCookie.setMaxAge(0);
				response.addCookie(expiredCookie);

				SecurityContextHolder.clearContext();
				System.out.println("JWT chứa user không tồn tại — cookie đã bị xóa tự động.");
			} catch (Exception e) {
				System.out.println("Lỗi xác thực JWT: " + e.getMessage());
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}
}
