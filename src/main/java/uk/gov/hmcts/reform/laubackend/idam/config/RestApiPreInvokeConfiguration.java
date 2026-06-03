
package uk.gov.hmcts.reform.laubackend.idam.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.hmcts.reform.laubackend.idam.authorization.RestApiPreInvokeDeleteInterceptor;
import uk.gov.hmcts.reform.laubackend.idam.authorization.RestApiPreInvokeInterceptor;

@Configuration
@RequiredArgsConstructor
public class RestApiPreInvokeConfiguration implements WebMvcConfigurer {

    private final RestApiPreInvokeInterceptor restApiPreInvokeInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(restApiPreInvokeInterceptor).addPathPatterns("/audit/**");
        registry.addInterceptor(restApiPreInvokeDeleteInterceptor())
                .addPathPatterns("/audit/logon/deleteAuditLogonRecord/**");
    }

    @Bean
    public RestApiPreInvokeDeleteInterceptor restApiPreInvokeDeleteInterceptor() {
        return new RestApiPreInvokeDeleteInterceptor();
    }
}
