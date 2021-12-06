package uk.gov.hmcts.reform.laubackend.idam.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;
import uk.gov.hmcts.reform.idam.client.IdamApi;

@Configuration
@Lazy
@EnableFeignClients(clients = IdamApi.class)
public class AuthServiceConfiguration {

    @Bean
    @ConditionalOnProperty(name = "idam.s2s-auth.url")
    public AuthTokenValidator tokenValidator(final ServiceAuthorisationApi s2sApi) {
        return new ServiceAuthTokenValidator(s2sApi);
    }
}

