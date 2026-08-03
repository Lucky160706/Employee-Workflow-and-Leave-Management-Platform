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
 * "Stateless" = server khong luu session cho ai dang nhap - moi request tu chung minh
 * danh tinh bang JWT trong header. CSRF (tan cong gia mao request tu 1 trang khac) chi
 * la moi de doa khi trinh duyet TU DONG gui cookie/session; o day khong dung cookie/session
 * nen tat CSRF la dung, khong phai bo qua an toan.
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
                // Mac dinh Spring Security tra 403 cho CA hai truong hop. Tach ra:
                //   401 = chua/khong con danh tinh hop le (chua dang nhap, token het han)
                //         -> client biet phai dang nhap lai;
                //   403 = da dang nhap nhung khong du quyen (giu nguyen, do accessDenied
                //         handler mac dinh xu ly) -> dang nhap lai cung vo ich.
                // HttpStatusEntryPoint la lop co san cua Spring Security, khong can tu viet.
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/integration/sso/validate",
                                // "/error": khi bi tu choi quyen, Spring goi
                                // response.sendError(403) - servlet container DISPATCH
                                // lai request sang /error de dung body loi. Luot dispatch
                                // do khong con SecurityContext (OncePerRequestFilter mac
                                // dinh bo qua ERROR dispatch), nen neu /error can xac thuc
                                // thi no bi chan tiep va status 403 ban dau bi ghi de.
                                "/error",
                                "/actuator/health/**",
                                "/actuator/info",
                                // Ca "/swagger-ui.html" LAN "/swagger-ui/**": pattern
                                // "/swagger-ui/**" khong khop "/swagger-ui.html" (khac
                                // segment), ma do lai dung la URL README bao nguoi dung mo.
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
     * Khong can tu tao DaoAuthenticationProvider: khi trong context co san 1 bean
     * UserDetailsService (UserDetailsServiceImpl) va 1 bean PasswordEncoder, Spring
     * Security tu noi chung lai thanh dung provider do. Tu khai bao lai chi lap y het
     * (va setter cua no da deprecated tu Security 6.4). LeaveRequestControllerIT dang
     * nhap that qua HTTP nen neu day khong con hoat dong, test do do ngay.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
