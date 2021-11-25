package uk.gov.hmcts.reform.laubackend.idam.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.servlet.http.HttpServletResponse;
import java.security.Security;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-ui.html",
                                   "/webjars/springfox-swagger-ui/**",
                                   "/swagger-resources/**",
                                   "/health",
                                   "/health/liveness",
                                   "/health/readiness",
                                   "/v2/api-docs/**",
                                   "/info",
                                   "/favicon.ico",
                                   "/");

    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse
                                                                                             .SC_UNAUTHORIZED))
            .and()
            .authorizeRequests()
            .antMatchers("/audit/logon/s**").authenticated();

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
}
