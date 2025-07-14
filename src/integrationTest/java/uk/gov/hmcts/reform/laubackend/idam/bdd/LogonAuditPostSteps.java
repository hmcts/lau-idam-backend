package uk.gov.hmcts.reform.laubackend.idam.bdd;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAudit;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAuditWithInvalidParameter;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAuditWithMissingMandatoryBodyParameter;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestHelper.postObject;

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

    @When("I POST {string} endpoint with invalid s2s")
    public void requestPostLogonAuditEndpointWith503Failure(final String path) {
        WIREMOCK.getWireMockServer().resetRequests();
        final Response response = restHelper.postObjectWithServiceUnavailableHeader(
                getLogonAudit(), baseUrl() + path);
        httpStatusResponseCode = response.getStatusCode();
    }

    @Then("http forbidden response is returned for POST logon")
    public void assertForbiddenResponse() {
        assertThat(httpStatusResponseCode).isEqualTo(FORBIDDEN.value());
    }

    @And("it should try making retry call for authorisation details")
    public void tryToRetryDetailsCall() {
        WIREMOCK.getWireMockServer().verify(3, WireMock.getRequestedFor(
            WireMock.urlPathEqualTo("/details")));
    }

    @When("I POST {string} endpoint with invalid s2s 401 failure")
    public void requestPostLogonAuditEndpointWith401Failure(final String path) {
        WIREMOCK.getWireMockServer().resetRequests();
        final Response response = restHelper.postObjectWithBadServiceHeader(
            getLogonAudit(), baseUrl() + path);
        httpStatusResponseCode = response.getStatusCode();
    }


    @And("it should not try making retry call for authorisation details")
    public void notRetryDetailsCall() {
        WIREMOCK.getWireMockServer().verify(1, WireMock.getRequestedFor(
            WireMock.urlPathEqualTo("/details")));
    }
}
