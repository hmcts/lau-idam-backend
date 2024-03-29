package uk.gov.hmcts.reform.laubackend.idam.bdd;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAudit;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAuditWithInvalidParameter;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAuditWithMissingMandatoryBodyParameter;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestHelper.postObject;

@SuppressWarnings({"PMD.JUnit4TestShouldUseBeforeAnnotation"})
public class LogonAuditPostSteps extends AbstractSteps {

    private String logonAuditPostResponseBody;
    private int httpStatusResponseCode;

    @Before
    public void setUp() {
        setupAuthorisationStub();
    }

    @When("I POST IdAM login using {string} endpoint using s2s")
    public void postValidLogon(final String path) {
        final Response response = postObject(getLogonAudit(), baseUrl() + path);
        assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        logonAuditPostResponseBody = response.getBody().asString();
    }

    @When("I POST {string} endpoint with missing request body parameter using s2s")
    public void postWithMissingBodyParameter(final String path) {
        final Response response = postObject(getLogonAuditWithMissingMandatoryBodyParameter(), baseUrl() + path);
        httpStatusResponseCode = response.getStatusCode();
    }

    @When("I POST {string} endpoint with invalid body parameter using s2s")
    public void postWithInvalidBodyParameter(final String path) {
        final Response response = postObject(getLogonAuditWithInvalidParameter(), baseUrl() + path);
        httpStatusResponseCode = response.getStatusCode();
    }

    @Then("http bad request response is returned for POST logon")
    public void assertBadRequestResponse() {
        assertThat(httpStatusResponseCode).isEqualTo(BAD_REQUEST.value());
    }

    @Then("logon response body is returned")
    public void logonResponseBodyIsReturned() {
        final LogonLogPostRequest logonAudit = getLogonAudit();
        final LogonLogPostResponse logonLogPostResponse = jsonReader
                .fromJson(logonAuditPostResponseBody, LogonLogPostResponse.class);

        assertThat(logonAudit.getLogonLog().getEmailAddress())
                .isEqualTo(logonLogPostResponse.getLogonLog().getEmailAddress());

        assertThat(logonAudit.getLogonLog().getIpAddress())
                .isEqualTo(logonLogPostResponse.getLogonLog().getIpAddress());

        assertThat(logonAudit.getLogonLog().getLoginState())
                .isEqualTo(logonLogPostResponse.getLogonLog().getLoginState());

        assertThat(logonAudit.getLogonLog().getService())
                .isEqualTo(logonLogPostResponse.getLogonLog().getService());

        assertThat(logonAudit.getLogonLog().getUserId())
                .isEqualTo(logonLogPostResponse.getLogonLog().getUserId());

        assertThat(logonAudit.getLogonLog().getTimestamp())
                .isEqualTo(logonLogPostResponse.getLogonLog().getTimestamp());
    }
}
