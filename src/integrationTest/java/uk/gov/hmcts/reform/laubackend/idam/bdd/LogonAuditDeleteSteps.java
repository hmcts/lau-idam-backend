package uk.gov.hmcts.reform.laubackend.idam.bdd;

import com.google.gson.Gson;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAudit;

@SuppressWarnings({"PMD.JUnit4TestShouldUseBeforeAnnotation"})
public class LogonAuditDeleteSteps extends AbstractSteps {

    private int httpStatusResponseCode;
    private String logonLogResponseBody;
    private final Gson jsonReader = new Gson();
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
        final Response response = restHelper.getResponse(baseUrl() + path, "userId", userId);
        logonLogResponseBody = response.getBody().asString();
    }

    @And("I request DELETE {string} logon endpoint with missing s2s header")
    public void deleteLogonLogWithMissingHeader(final String path) {
        final Response response = restHelper.deleteResponseWithoutServiceAuthHeader(baseUrl() + path);
        httpStatusResponseCode = response.getStatusCode();
    }

    @And("I request DELETE {string} logon endpoint with missing authorization header")
    public void deleteLogonLogWithMissingAuthHeader(final String path) {
        final Response response = restHelper.deleteResponseWithoutAuthHeader(baseUrl() + path);
        httpStatusResponseCode = response.getStatusCode();
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
