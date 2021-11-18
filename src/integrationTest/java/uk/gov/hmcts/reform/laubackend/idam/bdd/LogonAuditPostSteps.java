package uk.gov.hmcts.reform.laubackend.idam.bdd;

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

public class LogonAuditPostSteps extends AbstractSteps {

    private String logonAuditPostResponseBody;
    private int httpStatusResponseCode;

    @When("I POST IdAM login using {string} endpoint")
    public void postValidLogon(final String path) {
        final Response response = postObject(getLogonAudit(), baseUrl() + path);
        assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        logonAuditPostResponseBody = response.getBody().asString();
    }

    @When("I POST {string} endpoint with missing request body parameter")
    public void postWithMissingBodyParameter(final String path) {
        final Response response = postObject(getLogonAuditWithMissingMandatoryBodyParameter(), baseUrl() + path);
        httpStatusResponseCode = response.getStatusCode();
    }

    @When("I POST {string} endpoint with invalid body parameter")
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
        final LogonLogPostResponse caseActionPostResponse = jsonReader
                .fromJson(logonAuditPostResponseBody, LogonLogPostResponse.class);

        assertThat(logonAudit.getLogonLog().getEmailAddress())
                .isEqualTo(caseActionPostResponse.getLogonLog().getEmailAddress());

        assertThat(logonAudit.getLogonLog().getIpAddress())
                .isEqualTo(caseActionPostResponse.getLogonLog().getIpAddress());

        assertThat(logonAudit.getLogonLog().getService())
                .isEqualTo(caseActionPostResponse.getLogonLog().getService());

        assertThat(logonAudit.getLogonLog().getUserId())
                .isEqualTo(caseActionPostResponse.getLogonLog().getUserId());

        assertThat(logonAudit.getLogonLog().getTimestamp())
                .isEqualTo(caseActionPostResponse.getLogonLog().getTimestamp());
    }
}
