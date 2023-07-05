package uk.gov.hmcts.reform.laubackend.idam.bdd;

import com.google.gson.Gson;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionGetResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDeletionAuditDeleteSteps extends AbstractSteps {

    private final Gson jsonReader = new Gson();
    private Response response;

    @When("I request DELETE {string} with user id {string}")
    public void deleteUserDeletionAuditByUserId(String endpoint, String userId) {
        response = restHelper.deleteResponse(baseUrl() + endpoint, "userId", userId);
    }

    @Then("{int} response is returned")
    public void assertResponse(int statusCode) {
        assertThat(statusCode).isEqualTo(response.getStatusCode());
    }

    @Then("An empty GET {string} response returned for the params:")
    public void anEmptyGetResponseReturned(String endpoint, Map<String, String> params) {
        final Response getResponse = restHelper.getResponse(baseUrl() + endpoint, params);
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        var responseBody = jsonReader.fromJson(getResponse.getBody().asString(), UserDeletionGetResponse.class);
        assertThat(responseBody.getDeletionLogs()).isEmpty();
    }

    @Then("HTTP {string} {string} response is returned")
    public void assertHttpResponse(String statusCode, String status) {
        assertThat(Integer.parseInt(statusCode)).isEqualTo(response.getStatusCode());
    }
}
