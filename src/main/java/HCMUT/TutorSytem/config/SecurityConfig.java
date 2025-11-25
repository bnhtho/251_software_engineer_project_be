package HCMUT.TutorSytem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import HCMUT.TutorSytem.filter.AuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthorizationFilter authorizationFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // 🔹 RẤT QUAN TRỌNG: bật hỗ trợ CORS trong Spring Security
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> {
                    // 1. OPTIONS (preflight) - LUÔN ĐẦU TIÊN
                    request.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // 2. Login endpoint - PUBLIC
                    request.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();

                    // 3. ADMIN endpoints
                    request.requestMatchers("/admin/**").hasRole("ADMIN");

                    // 4. TUTOR endpoints CỤ THỂ (ĐẶT TRƯỚC /tutors chung)
                    // GET endpoints riêng cho tutor owner
                    request.requestMatchers(HttpMethod.GET, "/tutors/profile/**").hasRole("TUTOR");
                    request.requestMatchers(HttpMethod.GET, "/tutors/pending-registrations").hasRole("TUTOR");
                    request.requestMatchers(HttpMethod.GET, "/tutors/schedule/**").hasRole("TUTOR");
//                    // POST /tutors để đăng ký làm tutor - PUBLIC
//                    request.requestMatchers(HttpMethod.POST, "/tutors").permitAll(); --> Authenticated()
                    // PUT/DELETE tutors cho tutor owner
                    request.requestMatchers(HttpMethod.PUT, "/tutors/**").hasRole("TUTOR");
                    request.requestMatchers(HttpMethod.DELETE, "/tutors/**").hasRole("TUTOR");

                    // 5. TUTOR general endpoints - PUBLIC
                    // GET /tutors - danh sách tutor public
                    request.requestMatchers(HttpMethod.GET, "/tutors").permitAll();

                    // 6. SESSION endpoints
                    request.requestMatchers(HttpMethod.GET, "/sessions").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/sessions").hasRole("TUTOR");
                    request.requestMatchers(HttpMethod.PUT, "/sessions/**").hasRole("TUTOR");
                    request.requestMatchers(HttpMethod.DELETE, "/sessions/**").hasRole("TUTOR");

//                    // 7. STUDENT endpoints
//                    // Bảo vệ tất cả /students/** endpoints với role STUDENT
//                    request.requestMatchers("/students/**").hasRole("STUDENT");

                    // 8. Lookup/Reference endpoints - PUBLIC
                    request.requestMatchers(HttpMethod.GET, "/subjects").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/departments").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/majors/**").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/session-statuses").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/student-session-statuses").permitAll();

                    // Tutor profile registration
                    request.requestMatchers(HttpMethod.POST, "/api/tutor-profiles").hasRole("STUDENT");
                    request.requestMatchers(HttpMethod.PATCH, "/api/admin/tutor_profiles/**").hasRole("admin");
                    request.requestMatchers(HttpMethod.GET, "/api/admin/tutor_profiles/**").hasRole("admin");

                    // 9. Default - AUTHENTICATED
                    // Tất cả requests khác yêu cầu authentication
                    request.anyRequest().authenticated();
                })
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
