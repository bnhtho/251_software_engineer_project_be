package HCMUT.TutorSytem.config;


import HCMUT.TutorSytem.filter.AuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Using BCryptPasswordEncoder for password hashing
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationFilter authorizationFilter) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .authorizeHttpRequests(request -> {
                    // Session endpoints
                    request.requestMatchers(HttpMethod.GET, "/sessions").permitAll();  // Anyone can view sessions
                    request.requestMatchers(HttpMethod.POST, "/sessions").hasRole("TUTOR");  // Only tutors can create sessions
                    request.requestMatchers(HttpMethod.PUT, "/sessions/**").hasRole("TUTOR");  // Tutor owner can update (checked in controller)
                    request.requestMatchers(HttpMethod.DELETE, "/sessions/**").hasRole("TUTOR");  // Tutor owner can delete (checked in controller)

                    // Tutor endpoints
                    request.requestMatchers(HttpMethod.GET, "/tutors").permitAll();  // Anyone can view tutors
                    request.requestMatchers(HttpMethod.POST, "/tutors").permitAll();  // Anyone can register as tutor
                    request.requestMatchers(HttpMethod.PUT, "/tutors/**").hasRole("TUTOR");  // Tutor owner can update (checked in controller)
                    request.requestMatchers(HttpMethod.DELETE, "/tutors/**").hasRole("TUTOR");  // Tutor owner can delete (checked in controller)

                    // Lookup/Reference endpoints (public for form dropdowns)
                    request.requestMatchers(HttpMethod.GET, "/subjects").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/departments").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/majors/**").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/session-statuses").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/student-session-statuses").permitAll();

                    //Login endpoint
                    request.requestMatchers(HttpMethod.POST, "/auth/**  ").permitAll(); // Anyone can attempt to
                    // Default: require authentication for other requests
                    request.anyRequest().authenticated();
                })
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
