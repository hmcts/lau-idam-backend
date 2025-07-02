package uk.gov.hmcts.reform.laubackend.idam.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.reform.idam.client.IdamApi;

@Configuration
@Lazy
@EnableFeignClients(clients = IdamApi.class)
@ComponentScan(basePackages = {"uk.gov.hmcts.reform.idam"})
public class AuthServiceConfiguration {

}

