package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@Setter
@PropertySource("classpath:application-functional.yaml")
public class ConstantsResolver {

    @Value("${targetInstance}")
    private String apiUrl;
    @Value("${idam.grant.type}")
    private String idamGrantType;
    @Value("${idam.username}")
    private String idamUserName;
    @Value("${idam.password}")
    private String idamPassword;
    @Value("${idam.redirect.url}")
    private String idamRedirectUrl;
    @Value("${idam.token.url}")
    private String idamTokenUrl;

    public String getApiUrl() {
        return apiUrl;
    }

    public String getGrantType() {
        return idamGrantType;
    }

    public String getIdamUserName() {
        return idamUserName;
    }

    public String getIdamPassword() {
        return idamPassword;
    }

    public String getIdamRedirectUrl() {
        return idamRedirectUrl;
    }

    public String getIdamTokenUrl() {
        return idamTokenUrl;
    }
}
