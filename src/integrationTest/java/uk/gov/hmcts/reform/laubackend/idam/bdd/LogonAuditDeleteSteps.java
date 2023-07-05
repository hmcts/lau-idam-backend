package uk.gov.hmcts.reform.laubackend.idam.bdd;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAudit;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.END_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.END_TIME_PARAMETER;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.START_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.START_TIME_PARAMETER;

@SuppressWarnings({"PMD.JUnit4TestShouldUseBeforeAnnotation"})
public class LogonAuditDeleteSteps extends AbstractSteps {

    private int httpStatusResponseCode;
    private String logonLogResponseBody;

    private String logonId;

    @Before
    public void setUp() {
        setupAuthorisationStub();
    }

    @When("I request POST {string} logon endpoint using userId {string}")
    public void postCaseAction(final String path, final String userId) {
        final Response response = restHelper.postObject(getLogonAudit(userId), baseUrl() + path);

        assertThat(response.getStatusCode()).isEqualTo(CREATED.value());

        final LogonLogPostResponse logonLogPostResponse = jsonReader
                .fromJson(response.getBody().asString(), LogonLogPostResponse.class);

        logonId = logonLogPostResponse.getLogonLog().getId();

        assertThat(logonId).isNotEqualTo(null);
    }

    @And("I request DELETE {string} logon endpoint")
    public void deleteLogonLog(final String path) {
        final Response response = restHelper.deleteResponse(baseUrl() + path, "logonId", logonId);
        assertThat(response.getStatusCode()).isEqualTo(OK.value());
    }

    @And("I GET {string} logon using userId {string}")
    public void searchByLogonLogId(final String path, final String userId) {
        final Response response = restHelper.getResponse(baseUrl() + path,
                                 Map.of("userId", userId,
                                 START_TIME_PARAMETER, START_TIME, END_TIME_PARAMETER, END_TIME));
        logonLogResponseBody = response.getBody().asString();
    }

    @And("I request DELETE {string} logon endpoint with invalid logonId {string}")
    public void deleteLogonLogWithInvalidLogonLogId(final String path, final String logonId) {
        final Response response = restHelper.deleteResponse(baseUrl() + path, "logonId", logonId);
        httpStatusResponseCode = response.getStatusCode();
    }

    @And("I request DELETE {string} logon endpoint with missing logonId")
    public void deleteLogonLogWithMissingLogonLogId(final String path) {
        final Response response = restHelper.deleteResponse(baseUrl() + path, "logonId", null);
        httpStatusResponseCode = response.getStatusCode();
    }

    @Then("http {string} response is returned for DELETE logon")
    public void assertHttpResponse(final String httpResponse) {
        assertThat(Integer.parseInt(httpResponse)).isEqualTo(httpStatusResponseCode);
    }

    @Then("An empty result for DELETE logon is returned")
    public void assertResponse() {
        final LogonLogGetResponse caseActionGetResponse = jsonReader
                .fromJson(logonLogResponseBody, LogonLogGetResponse.class);

        assertThat(caseActionGetResponse.getLogonLog().size()).isEqualTo(0);
    }
}
