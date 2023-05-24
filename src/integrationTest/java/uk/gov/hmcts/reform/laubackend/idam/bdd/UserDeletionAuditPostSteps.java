package uk.gov.hmcts.reform.laubackend.idam.bdd;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.request.UserDeletionPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionPostResponse;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestHelper.postObject;

public class UserDeletionAuditPostSteps extends AbstractSteps {

    private String userDeletionResponseBody;
    private HttpStatus responseStatus;

    @DataTableType
    public DeletionLog deletionLog(Map<String, String> entry) {
        return new DeletionLog(
            entry.get("user id"),
            entry.get("email address"),
            entry.get("first name"),
            entry.get("last name"),
            entry.get("deletion timestamp")
        );
    }

    @When("I POST IdAM user deletion data to {string} endpoint using s2s with data:")
    public void postUserDeletionData(final String path, List<DeletionLog> data) {
        var payload = new UserDeletionPostRequest();
        payload.setDeletionLogs(data);
        var response = postObject(payload, baseUrl() + path);
        responseStatus = HttpStatus.valueOf(response.getStatusCode());
        userDeletionResponseBody = response.getBody().asString();
    }

    @Then("The same user deletions are returned:")
    public void userDeletionAuditReturned(List<DeletionLog> data) {
        final var response = jsonReader.fromJson(userDeletionResponseBody, UserDeletionPostResponse.class);
        assertThat(response.getDeletionLogs()).hasSize(2);

        for (int i = 0; i < data.size(); i++) {
            var requested = data.get(i);
            var returned = response.getDeletionLogs().get(i);
            assertEquals(requested.getUserId(), returned.getUserId(), "User ids do not match");
            assertEquals(requested.getEmailAddress(), returned.getEmailAddress(), "Emails do not match");
            assertEquals(requested.getFirstName(), returned.getFirstName(), "First names do not match");
            assertEquals(requested.getLastName(), returned.getLastName(), "Last names do not match");
            assertEquals(
                requested.getDeletionTimestamp(),
                returned.getDeletionTimestamp(),
                "Timestamps do not match"
            );
        }
    }

    @Then("{string} response status returned")
    public void badRequestResponseReturned(String httpStatus) {
        assertEquals(httpStatus, responseStatus.name(), "Status code is not what was expected");
    }
}
