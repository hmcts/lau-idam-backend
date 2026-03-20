package uk.gov.hmcts.reform.laubackend.idam.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Security;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfiguration {

    @Bean
    public BearerHeaderAuthenticationFilter bearerHeaderAuthenticationFilter() {
        return new BearerHeaderAuthenticationFilter();
    }

    @Bean
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exc -> exc.authenticationEntryPoint(
                (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            )
            .addFilterBefore(bearerHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authz -> authz
                // Require an Authorization header-based authentication for GET/DELETE audit endpoints
                .requestMatchers(GET, "/audit/**").authenticated()
                .requestMatchers(DELETE, "/audit/**").authenticated()
                // Everything else remains open (service/user auth enforced by MVC interceptors)
                .requestMatchers("/**").permitAll()
            );

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        return httpSecurity.build();
    }
}
