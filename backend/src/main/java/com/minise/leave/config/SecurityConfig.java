package com.minise.leave.config;

import com.minise.leave.security.CorrelationIdFilter;
import com.minise.leave.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Khong dung session - moi request tu mang JWT di chung minh danh tinh.
 * Nen tat CSRF o day khong phai la ha thap bao mat: CSRF chi khai thac duoc khi
 * trinh duyet tu dong gui kem cookie, ma ta khong dung cookie.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorrelationIdFilter correlationIdFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CorrelationIdFilter correlationIdFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.correlationIdFilter = correlationIdFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Mac dinh Spring tra 403 cho ca hai truong hop, kho chiu cho phia client.
                // Tach ra: 401 khi chua co danh tinh hop le (chua dang nhap hoac token het
                // han), 403 khi dang nhap roi nhung khong du quyen. Frontend dua vao day
                // de biet luc nao nen da nguoi dung ve man login, luc nao thi dung.
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/integration/sso/validate",
                                // Phai mo "/error", khong thi 403 bi bien thanh 401:
                                // luc tu choi quyen, servlet container dispatch lai
                                // request sang day de dung body loi, ma luot dispatch do
                                // khong con SecurityContext nua. De /error doi xac thuc
                                // thi no bi chan tiep va status ban dau bi ghi de mat.
                                "/error",
                                "/actuator/health/**",
                                "/actuator/info",
                                // Can ca hai dong duoi: "/swagger-ui/**" khong khop
                                // "/swagger-ui.html" vi khac segment, ma do lai dung la
                                // duong dan README bao nguoi dung mo.
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Khong tu tao DaoAuthenticationProvider: da co bean UserDetailsService va
     * PasswordEncoder trong context thi Spring Security tu noi chung lai. Khai bao lai
     * vua thua, vua dinh setter da deprecated tu Security 6.4.
     * LeaveRequestControllerIT dang nhap that qua HTTP nen hong cho nay la no do ngay.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
