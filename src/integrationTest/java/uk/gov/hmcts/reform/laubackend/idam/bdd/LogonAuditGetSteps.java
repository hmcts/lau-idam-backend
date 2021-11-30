package uk.gov.hmcts.reform.laubackend.idam.bdd;

import com.google.gson.Gson;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static uk.gov.hmcts.reform.laubackend.idam.helper.LogonAuditGetHelper.getLogonLogPostRequest;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.JUnit4TestShouldUseBeforeAnnotation"})
public class LogonAuditGetSteps extends AbstractSteps {

    private String logonLogPostResponseBody;
    private final Gson jsonReader = new Gson();

    @Before
    public void setUp() {
        setupServiceAuthorisationStub();
    }

    @When("I POST multiple records to {string} endpoint using {string} userIds")
    public void postCaseActionForUserIds(final String path, final String pathParam) {
        final List<String> pathParams = asList(pathParam.split(","));
        pathParams.forEach(userId -> {
            final Response response = restHelper.postObject(getLogonLogPostRequest(userId,
                            null,
                            null,
                            null,
                            null),
                    baseUrl() + path);

            assertThat(response.getStatusCode()).isEqualTo(CREATED.value());
        });
    }

    @And("I GET {string} using userId {string} query param")
    public void searchUsingUserId(final String path, String userId) {
        final Response response = restHelper.getResponse(baseUrl() + path, "userId", userId);
        logonLogPostResponseBody = response.getBody().asString();
    }

    @Then("a single logon response body is returned for userId {string}")
    public void assertResponse(final String userId) {
        final LogonLogGetResponse logonLogGetResponse = jsonReader
                .fromJson(logonLogPostResponseBody, LogonLogGetResponse.class);
        final LogonLogPostRequest logonLogPostRequest = getLogonLogPostRequest(userId, null, null, null, null);

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
    }
}
