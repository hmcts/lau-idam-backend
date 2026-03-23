package uk.gov.hmcts.reform.laubackend.idam.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.security.Security;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfiguration {

    @Bean
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                                   session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exc ->
                                   exc.authenticationEntryPoint(
                                       (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                                   )
            )
            .authorizeHttpRequests(authz -> authz
                //Interceptor is checking ServiceAuthorization Header for post as no Authorization header is expected 
                // for post request, hence permitting all post requests to /audit/** endpoint. 
                // For get and delete request, both ServiceAuthorization and 
                // Authorization header is expected, hence authenticated.
                .requestMatchers(HttpMethod.POST, "/audit/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/audit/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/audit/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 ->
                                      oauth2.jwt(withDefaults())
            );

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        return httpSecurity.build();
    }
}
