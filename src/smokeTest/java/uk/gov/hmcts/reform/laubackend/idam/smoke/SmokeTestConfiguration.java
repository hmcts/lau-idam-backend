package uk.gov.hmcts.reform.laubackend.idam.smoke;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan("uk.gov.hmcts.reform.laubackend.idam.smoke")
@PropertySource("application.properties")
public class SmokeTestConfiguration {
}
