package HCMUT.TutorSytem.filter;

import HCMUT.TutorSytem.util.JWTHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Autowired
    private JWTHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        log.info("========== AuthorizationFilter START ==========");
        log.info("Request URI: {} {}", request.getMethod(), request.getRequestURI());
        log.info("Authorization Header: {}", header != null ? "Present" : "Not Present");

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(7);
            log.info("JWT Token extracted (first 20 chars): {}...", token.length() > 20 ? token.substring(0, 20) : token);

            try {
                boolean valid = jwtHelper.isValid(token);
                log.info("Token validation result: {}", valid);

                if (valid) {
                    String userIdStr = jwtHelper.getUserIdFromToken(token);
                    String role = jwtHelper.getRoleFromToken(token);

                    log.info("UserId from token: {}", userIdStr);
                    log.info("Role from token (original): {}", role);

                    if (userIdStr != null && role != null) {
                        // Convert userId từ String sang Integer
                        Integer userId = Integer.parseInt(userIdStr);
                        log.info("Parsed UserId as Integer: {}", userId);

                        // Uppercase role để đảm bảo match với SecurityConfig hasRole("ADMIN")
                        String roleUpperCase = role.toUpperCase();
                        log.info("Role after uppercase: {}", roleUpperCase);

                        List<GrantedAuthority> authorities = new ArrayList<>();
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleUpperCase);
                        authorities.add(authority);

                        log.info("Authorities created: {}", authorities);
                        log.info("Number of authorities: {}", authorities.size());
                        authorities.forEach(auth -> log.info("  - Authority: {}", auth.getAuthority()));

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);

                        log.info("Authentication set in SecurityContext successfully");
                        log.info("Principal (userId): {}", authentication.getPrincipal());
                        log.info("Authorities in authentication: {}", authentication.getAuthorities());
                    } else {
                        log.warn("UserId or Role is null - Authentication not set");
                    }
                } else {
                    log.warn("Token is invalid - Authentication not set");
                }
            } catch (Exception e) {
                log.error("Error during JWT processing: {}", e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        } else {
            if (header == null) {
                log.info("No Authorization header present");
            } else if (!header.startsWith("Bearer ")) {
                log.warn("Authorization header does not start with 'Bearer '");
            } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.info("Authentication already set in SecurityContext");
            }
        }

        // Log thông tin về endpoint matching và required authorities
        log.info("---------- Security Check Info ----------");
        log.info("Request: {} {}", request.getMethod(), request.getRequestURI());

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("User authenticated: YES");
            log.info("Principal: {}", auth.getPrincipal());
            log.info("Current authorities: {}", auth.getAuthorities());
            log.info("Is authenticated: {}", auth.isAuthenticated());
        } else {
            log.info("User authenticated: NO");
        }

        // Log matching rules để biết endpoint này cần role gì
        String uri = request.getRequestURI();
        String method = request.getMethod();
        log.info("---------- Security Rules Matching ----------");

        if (uri.startsWith("/admin/")) {
            log.info("⚠️ Endpoint matches: /admin/** → Requires: ROLE_ADMIN");
        } else if (method.equals("GET") && uri.matches("/tutors/profile/.*")) {
            log.info("⚠️ Endpoint matches: GET /tutors/profile/** → Requires: ROLE_TUTOR");
        } else if (method.equals("GET") && uri.equals("/tutors/pending-registrations")) {
            log.info("⚠️ Endpoint matches: GET /tutors/pending-registrations → Requires: ROLE_TUTOR");
        } else if (method.equals("GET") && uri.matches("/tutors/schedule/.*")) {
            log.info("⚠️ Endpoint matches: GET /tutors/schedule/** → Requires: ROLE_TUTOR");
        } else if (method.equals("PUT") && uri.matches("/tutors/.*")) {
            log.info("⚠️ Endpoint matches: PUT /tutors/** → Requires: ROLE_TUTOR");
        } else if (method.equals("DELETE") && uri.matches("/tutors/.*")) {
            log.info("⚠️ Endpoint matches: DELETE /tutors/** → Requires: ROLE_TUTOR");
        } else if (method.equals("GET") && uri.equals("/tutors")) {
            log.info("✅ Endpoint matches: GET /tutors → Public (permitAll)");
        } else if (method.equals("GET") && uri.equals("/sessions")) {
            log.info("✅ Endpoint matches: GET /sessions → Public (permitAll)");
        } else if (method.equals("POST") && uri.equals("/sessions")) {
            log.info("⚠️ Endpoint matches: POST /sessions → Requires: ROLE_TUTOR");
        } else if (method.equals("PUT") && uri.matches("/sessions/.*")) {
            log.info("⚠️ Endpoint matches: PUT /sessions/** → Requires: ROLE_TUTOR");
        } else if (method.equals("DELETE") && uri.matches("/sessions/.*")) {
            log.info("⚠️ Endpoint matches: DELETE /sessions/** → Requires: ROLE_TUTOR");
        } else if (uri.startsWith("/students/")) {
            log.info("⚠️ Endpoint matches: /students/** → Requires: ROLE_STUDENT");
        } else if (method.equals("POST") && uri.startsWith("/auth/")) {
            log.info("✅ Endpoint matches: POST /auth/** → Public (permitAll)");
        } else if (method.equals("GET") && (uri.equals("/subjects") || uri.equals("/departments")
                || uri.startsWith("/majors") || uri.equals("/session-statuses")
                || uri.equals("/student-session-statuses"))) {
            log.info("✅ Endpoint matches: Lookup/Reference endpoint → Public (permitAll)");
        } else {
            log.info("⚠️ Endpoint matches: Default rule → Requires: Authenticated");
        }

        log.info("========== AuthorizationFilter END ==========");
        filterChain.doFilter(request, response);
    }
}
