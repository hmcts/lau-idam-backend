package uk.gov.hmcts.reform.laubackend.idam.client;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.http.HttpStatus;

@Slf4j
@SuppressWarnings({"unchecked"})
public class LauIdamBackEndServiceClient {

    private final String lauIdamBackEndApiUrl;

    public LauIdamBackEndServiceClient(String lauIdamBackEndApiUrl) {
        this.lauIdamBackEndApiUrl = lauIdamBackEndApiUrl;
    }

    public String getWelcomePage() {
        return SerenityRest
            .get(lauIdamBackEndApiUrl)
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .extract()
            .body()
            .asString();
    }

    public String getHealthPage() {
        return SerenityRest
            .get(lauIdamBackEndApiUrl + "/health")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .extract()
            .body()
            .asString();
    }
}
