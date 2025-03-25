package uk.gov.hmcts.reform.laubackend.idam.bdd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.commons.beanutils.BeanUtilsBean;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.request.UserDeletionPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionGetResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestHelper.postObject;

public class UserDeletionAuditGetSteps extends AbstractSteps {

    private String responseBodyString;
    private int httpStatusResponseCode;

    private static boolean doneIt;


    @When("I save several IdAM user deletions to {string} endpoint using s2s with data:")
    public void postUserDeletionData(final String path, List<DeletionLog> data) {
        if (!doneIt) {
            UserDeletionPostRequest payload = new UserDeletionPostRequest();
            payload.setDeletionLogs(data);
            Response response = postObject(payload, baseUrl() + path);
            httpStatusResponseCode = response.getStatusCode();
            assertEquals(201, httpStatusResponseCode, "Saving dummy deleted accounts failed");
            doneIt = true;
        }
    }

    @When("I GET {string} passing params:")
    public void makeGetRequestPassingParams(String path, Map<String, String> params) {
        final Response response = restHelper.getResponse(baseUrl() + path, params);
        httpStatusResponseCode = response.getStatusCode();
        responseBodyString = response.getBody().asString();
    }

    @Then("a single deleted account entry is returned with params {string} {string}")
    public void singleDeletedAccountEntryIsReturned(String name, String value)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final UserDeletionGetResponse response = jsonReader
            .fromJson(responseBodyString, UserDeletionGetResponse.class);
        final BeanUtilsBean bub = new BeanUtilsBean();
        assertThat(response.getDeletionLogs()).hasSize(1);
        String val = bub.getProperty(response.getDeletionLogs().get(0), name);
        assertEquals(value, val, "Properties do not match");
    }

    @Then("multiple deleted account entries are returned with params {string} {string}")
    public void multipleAccountEntriesReturned(String name, String value)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final UserDeletionGetResponse response = jsonReader
            .fromJson(responseBodyString, UserDeletionGetResponse.class);
        final BeanUtilsBean bub = new BeanUtilsBean();

        for (DeletionLog deletionLog: response.getDeletionLogs()) {
            String val = bub.getProperty(deletionLog, name);
            assertEquals(value, val, "Properties do not match");
        }
    }

    @Then("{int} {string} response is returned")
    public void responseIsReturned(int statusCode, String status) {
        String msg = String.format("Expected %d (%s), but got %d", statusCode, status, httpStatusResponseCode);
        assertEquals(statusCode, httpStatusResponseCode, msg);

    }

    @Then("multiple deleted account entries are returned with size {int}")
    public void multipleAccountEntriesReturnedWithCount(Integer count) {
        final UserDeletionGetResponse response = jsonReader
            .fromJson(responseBodyString, UserDeletionGetResponse.class);
        int size = response.getDeletionLogs().size();
        assertEquals(count, size, "Size does not match");

    }

    @Then("first deleted account entry is returned with params {string} {string}")
    public void firstDeletedAccountEntryIsReturned(String name, String value)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final UserDeletionGetResponse response = jsonReader
            .fromJson(responseBodyString, UserDeletionGetResponse.class);
        final BeanUtilsBean bub = new BeanUtilsBean();
        assertThat(response.getDeletionLogs()).hasSize(4);
        String val = bub.getProperty(response.getDeletionLogs().get(0), name);
        assertEquals(value, val, "Properties do not match");
    }

    @Then("two results are returned on the page {int}:")
    public void pagedResultsAreReturned(int page, List<DeletionLog> data) {
        final UserDeletionGetResponse response = jsonReader
            .fromJson(responseBodyString, UserDeletionGetResponse.class);
        assertThat(response.getDeletionLogs()).hasSize(2);
        assertThat(response.getTotalNumberOfRecords()).isEqualTo(4);
        assertThat(response.getMoreRecords()).isEqualTo(page == 1);
        assertThat(response.getStartRecordNumber()).isEqualTo((page - 1) * 2L + 1);
        DeletionLog expected1 = data.get(0);
        DeletionLog returned1 = response.getDeletionLogs().get(0);
        DeletionLog expected2 = data.get(1);
        DeletionLog returned2 = response.getDeletionLogs().get(1);

        assertThat(returned1.getUserId()).isEqualTo(expected1.getUserId());
        assertThat(returned2.getUserId()).isEqualTo(expected2.getUserId());
    }



}
