package uk.gov.hmcts.reform.laubackend.idam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI lauIdamBackendOpenApi() {
        License licence = new License().name("REFORM Common Components").url("");

        Info info = new Info().title("LAU IdAM Back-End API")
            .description("Log and Audit IdAM Back-End API, used for auditing IdAM logons.")
            .version("0.1")
            .license(licence);

        return new OpenAPI().info(info);
    }
}
