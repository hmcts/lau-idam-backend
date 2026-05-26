package uk.gov.hmcts.reform.laubackend.idam.controllers;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
class EndpointSecurityTest extends LauIdamBaseFunctionalTest {

    @Test
    void shouldAllowUnauthenticatedRequestsToWelcomeMessageAndReturn200ResponseCode() {

        String response = lauIdamBackEndServiceClient.getWelcomePage();

        assertThat(response).contains("Welcome");
    }

    @Test
    void shouldAllowUnauthenticatedRequestsToHealthCheckAndReturn200ResponseCode() {

        String response = lauIdamBackEndServiceClient.getHealthPage();

        assertThat(response).contains("UP");
    }
}
