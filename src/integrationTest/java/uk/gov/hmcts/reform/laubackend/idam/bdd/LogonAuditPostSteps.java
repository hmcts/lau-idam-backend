package uk.gov.hmcts.reform.laubackend.idam.bdd;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAudit;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAuditWithInvalidParameter;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditPostHelper.getLogonAuditWithMissingMandatoryBodyParameter;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestHelper.postObject;

@SuppressWarnings({"PMD.JUnit4TestShouldUseBeforeAnnotation","PMD.DoNotUseThreads",
    "PMD.SignatureDeclareThrowsException"})
public class LogonAuditPostSteps extends AbstractSteps {

    private static final String THREAD_NAME = "threadName";
    private static final String RESPONSE = "response";
    private static final int FAILURE_ID = 3;

    private String logonAuditPostResponseBody;
    private int httpStatusResponseCode;

    @Before
    public void setUp() {
        setupAuthorisationStub();
    }

    @After
    public void clearScenarioContext() {
        ScenarioContext.clear();
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

    @When("I POST 10 request to {string} endpoint with s2s in asynchronous mode")
    public void requestMultiplePostLogonAuditEndpointWithS2S(final String path) throws Exception {
        List<CompletableFuture<Response>> futures = new ArrayList<>();
        int numRequests = 10;

        for (int i = 0; i < numRequests; i++) {
            final int idx = i;
            futures.add(CompletableFuture.supplyAsync(() -> {
                String threadName = Thread.currentThread().getName();
                ScenarioContext.set(THREAD_NAME + idx, threadName);
                return restHelper.postObject(getLogonAudit(), baseUrl() + path);
            }));
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (int i = 0; i < numRequests; i++) {
            Response response = futures.get(i).get();
            ScenarioContext.set(RESPONSE + i, response);
            String threadName = ScenarioContext.get(THREAD_NAME + i);
            assertThat(threadName).isNotEqualTo("main");
            assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        }
    }

    @Then("logon response body is returned for all ten requests")
    public void logonResponseBodyIsReturnedForAllTenRequests() {
        for (int i = 0; i < 10; i++) {
            Response response = ScenarioContext.get(RESPONSE + i);
            assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        }
    }

    @When("I POST 10 request to {string} endpoint with s2s with simulate failures")
    public void requestMultiplePostLogonAuditEndpointWithFailures(final String path) throws Exception {

        List<CompletableFuture<Response>> futures = new ArrayList<>();
        final int numRequests = 10;

        for (int i = 0; i < numRequests; i++) {
            final int idx = i;
            futures.add(CompletableFuture.supplyAsync(() -> {
                String threadName = Thread.currentThread().getName();
                ScenarioContext.set(THREAD_NAME + idx, threadName);
                if (idx == FAILURE_ID) {
                    return restHelper.postObjectWithBadServiceHeader(
                        getLogonAudit(), baseUrl() + path);
                } else {
                    return restHelper.postObject(getLogonAudit(), baseUrl() + path);
                }
            }).exceptionally(ex -> {
                ScenarioContext.set("error" + idx, ex.getMessage());
                return null; // Return null for failed requests
            }));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (int i = 0; i < numRequests; i++) {
            CompletableFuture<Response> future = futures.get(i);
            if (!future.isCompletedExceptionally()) {
                Response response = future.get();
                String threadName = ScenarioContext.get(THREAD_NAME + i);
                assertThat(threadName).isNotEqualTo("main");
                ScenarioContext.set(RESPONSE + i, response);
            }
        }
    }

    @Then("logon response body is returned for passed requests with some failures")
    public void caseSearchResponseBodyIsReturnedForAllTenRequestsWithFailures() {
        for (int i = 0; i < 10; i++) {
            Response response = ScenarioContext.get(RESPONSE + i);
            if (response != null) {
                if (i == FAILURE_ID) {
                    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN.value());
                } else {
                    assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
                }
            }
        }
    }
}
