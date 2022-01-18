
package uk.gov.hmcts.reform.laubackend.idam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.hmcts.reform.laubackend.idam.authorization.RestApiPreInvokeDeleteInterceptor;
import uk.gov.hmcts.reform.laubackend.idam.authorization.RestApiPreInvokeInterceptor;

@Configuration
public class RestApiPreInvokeConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(restApiPreInvokeInterceptor()).addPathPatterns("/audit/**");
        registry.addInterceptor(restApiPreInvokeDeleteInterceptor())
                .addPathPatterns("/audit/logon/deleteAuditLogonRecord/**");
    }

    @Bean
    public RestApiPreInvokeInterceptor restApiPreInvokeInterceptor() {
        return new RestApiPreInvokeInterceptor();
    }

    @Bean
    public RestApiPreInvokeDeleteInterceptor restApiPreInvokeDeleteInterceptor() {
        return new RestApiPreInvokeDeleteInterceptor();
    }
}
