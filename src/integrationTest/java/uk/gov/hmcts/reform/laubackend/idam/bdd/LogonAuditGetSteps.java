package uk.gov.hmcts.reform.laubackend.idam.bdd;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.Gson;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditGetHelper.getLogonLogPostRequest;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.END_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.END_TIME_PARAMETER;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.START_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.START_TIME_PARAMETER;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.JUnit4TestShouldUseBeforeAnnotation",
    "PMD.UseObjectForClearerAPI"})
public class LogonAuditGetSteps extends AbstractSteps {

    private String logonLogPostResponseBody;
    private final Gson jsonReader = new Gson();

    @Before
    public void setUp() {
        setupAuthorisationStub();
    }

    @When("I POST multiple records to {string} endpoint using {string} userIds")
    public void postAuditLogonForUserIds(final String path, final String pathParam) {
        final List<String> pathParams = asList(pathParam.split(","));
        pathParams.forEach(userId -> {
            final Response response = restHelper.postObject(getLogonLogPostRequest(userId,
                            null,
                            null,
                            null,
                                    null,
                            null),
                    baseUrl() + path);

            assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        });
    }

    @When("I POST multiple records to {string} endpoint using {string} emailAddresses")
    public void postIdamLogonForEmailAddresses(final String path, final String pathParam) {
        final List<String> pathParams = asList(pathParam.split(","));
        pathParams.forEach(emailAddress -> {
            final Response response = restHelper.postObject(getLogonLogPostRequest(null,
                            emailAddress,
                            null,
                            null,
                            null,
                            null),
                    baseUrl() + path);

            assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        });
    }

    @When("I POST multiple records to {string} endpoint using {string} timestamp")
    public void postWithTimestamp(final String path, final String timestamp) {
        final List<String> pathParams = asList(timestamp.split(","));
        pathParams.forEach(timestampParam -> {
            final Response response = restHelper.postObject(getLogonLogPostRequest(null,
                            null,
                            null,
                            null,
                            null,
                            timestampParam),
                    baseUrl() + path);

            assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        });
    }

    @And("And I GET {string} using userId {string} query param")
    public void searchUsingUserId(final String path, String userId) {
        final Response response = restHelper.getResponse(baseUrl() + path,
                                    Map.of("userId", userId,
                                    START_TIME_PARAMETER, START_TIME,
                                    END_TIME_PARAMETER, END_TIME));
        logonLogPostResponseBody = response.getBody().asString();
    }

    @And("And I GET {string} using emailAddress {string} query param")
    public void searchUsingEmailAddress(final String path, String emailAddress) {
        final Response response = restHelper.getResponse(baseUrl() + path,
                                    Map.of("emailAddress", emailAddress,
                                    START_TIME_PARAMETER, START_TIME,
                                    END_TIME_PARAMETER, END_TIME));
        logonLogPostResponseBody = response.getBody().asString();
    }

    @And("And I GET {string} using startTimestamp {string} endTimestamp {string} userId {string} query param")
    public void retrieveWithStartTimeAndEndTime(final String path, final String startTimestamp,
                                                final String endTimeStamp, final String userId) {
        final Response response = restHelper.getResponse(baseUrl() + path,
                                    Map.of("userId", userId,
                                    START_TIME_PARAMETER, startTimestamp,
                                    END_TIME_PARAMETER, endTimeStamp));
        logonLogPostResponseBody = response.getBody().asString();
    }

    @Then("a single logon response body is returned for userId {string}")
    public void assertResponse(final String userId) {
        final LogonLogGetResponse logonLogGetResponse = jsonReader
                .fromJson(logonLogPostResponseBody, LogonLogGetResponse.class);
        final LogonLogPostRequest logonLogPostRequest = getLogonLogPostRequest(userId, null, null, null, null,null);

        assertObject(logonLogGetResponse, logonLogPostRequest);
    }

    @Then("a single logon response body is returned for emailAddress {string}")
    public void assertEmailAddressResponse(final String emailAddress) {
        final LogonLogGetResponse logonLogGetResponse = jsonReader
                .fromJson(logonLogPostResponseBody, LogonLogGetResponse.class);
        final LogonLogPostRequest logonLogPostRequest = getLogonLogPostRequest(null, emailAddress, null, null, null,
                null);

        assertObject(logonLogGetResponse, logonLogPostRequest);
    }

    @Then("a single logon response body is returned for startTimestamp {string}")
    public void assertStartTimeResponse(final String startTimestamp) {
        final LogonLogGetResponse logonLogGetResponse = jsonReader
                .fromJson(logonLogPostResponseBody, LogonLogGetResponse.class);
        final LogonLogPostRequest logonLogPostRequest = getLogonLogPostRequest(null, null, null, null,
                null, startTimestamp);

        assertObject(logonLogGetResponse, logonLogPostRequest);
    }

    @Then("a single logon response body is returned for endTimestamp {string}")
    public void assertWithEndTimestamp(final String endTimestamp) {
        final LogonLogGetResponse logonLogGetResponse = jsonReader
                .fromJson(logonLogPostResponseBody, LogonLogGetResponse.class);
        final LogonLogPostRequest logonLogPostRequest = getLogonLogPostRequest(null, null, null, null, null,
                endTimestamp);

        assertObject(logonLogGetResponse, logonLogPostRequest);
    }

    private void assertObject(final LogonLogGetResponse logonLogGetResponse,
                              final LogonLogPostRequest logonLogPostRequest) {
        assertThat(logonLogGetResponse.getLogonLog().size()).isEqualTo(1);
        assertThat(logonLogGetResponse.getLogonLog().get(0).getUserId())
                .isEqualTo(logonLogPostRequest.getLogonLog().getUserId());
        assertThat(logonLogGetResponse.getLogonLog().get(0).getService())
                .isEqualTo(logonLogPostRequest.getLogonLog().getService());
        assertThat(logonLogGetResponse.getLogonLog().get(0).getEmailAddress())
                .isEqualTo(logonLogPostRequest.getLogonLog().getEmailAddress());
        assertThat(logonLogGetResponse.getLogonLog().get(0).getIpAddress())
                .isEqualTo(logonLogPostRequest.getLogonLog().getIpAddress());
        assertThat(logonLogGetResponse.getLogonLog().get(0).getLoginState())
                .isEqualTo(logonLogPostRequest.getLogonLog().getLoginState());
    }

    @And("authorization End Point is called only once")
    public void assertErrorResponse() {
        WIREMOCK.getWireMockServer().verify(1, WireMock.getRequestedFor(
            WireMock.urlPathEqualTo("/details")));
    }
}
