package uk.gov.hmcts.reform.laubackend.idam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.hmcts.reform.laubackend.idam.Application;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.basePackage(Application.class.getPackage().getName() + ".controllers"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiDetails());
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
            "LAU IdAM Back-End API",
            "Log and Audit IdAM Back-End API, used for auditing IdAM logons.",
            "0.1",
            "",
            new Contact("", "", ""),
            "REFORM Common Components.",
            "",
            Collections.emptyList()
        );
    }

}
