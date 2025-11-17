package HCMUT.TutorSytem.filter;

import HCMUT.TutorSytem.util.JWTHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTHelper jwtHelper;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(7);

            try {
                boolean valid = jwtHelper.isValid(token);
                if (valid) {
                    String userIdStr = jwtHelper.getUserIdFromToken(token);
                    String role = jwtHelper.getRoleFromToken(token); // ví dụ: ADMIN / STUDENT

                    if (userIdStr != null && role != null) {
                        // Convert userId từ String sang Integer
                        Integer userId = Integer.parseInt(userIdStr);

                        List<GrantedAuthority> authorities = new ArrayList<>();
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        authorities.add(authority);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
