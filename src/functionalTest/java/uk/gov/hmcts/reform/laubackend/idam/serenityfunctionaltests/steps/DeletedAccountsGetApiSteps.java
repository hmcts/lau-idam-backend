package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import net.thucydides.core.annotations.Step;
import org.json.JSONException;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountsRequest;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountSearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeletedAccountsGetApiSteps extends BaseSteps {

    @Step("Given the GET service body is generated")
    public DeletedAccountsRequest generateDeletedAccountsRequest() {
        DeletedAccountSearch deletedAccountSearch = DeletedAccountSearch.builder()
            .userId("user id")
            .emailAddress("email@example.net")
            .firstName("John")
            .lastName("Smith")
            .startTimestamp("2023-05-23T09:34:23.432Z")
            .endTimestamp("2023-06-24T09:34:23.432Z")
            .build();

        DeletedAccountsRequest request = new DeletedAccountsRequest();
        request.setDeletionSearchLogs(Arrays.asList(deletedAccountSearch));
        return request;
    }

    @Step("Given invalid GET body is generated")
    public DeletedAccountsRequest generateInvalidDeletedAccountsSearchRequest() {
        DeletedAccountSearch deletedAccountSearch = DeletedAccountSearch.builder()
            .userId("user id")
            .emailAddress("")
            .firstName("John")
            .lastName("Smith")
            .endTimestamp("2023-05-23T09:34:23.432Z")
            .build();

        DeletedAccountsRequest request = new DeletedAccountsRequest();
        request.setDeletionSearchLogs(Arrays.asList(deletedAccountSearch));
        return request;
    }

    @Step("And valid Authorization token is generated")
    public String validAuthorizationTokenIsGenerated() throws JSONException {
        return authorizationHeaderHelper.getAuthorizationToken();
    }

    @Step("When valid params are supplied for Get Logon API")
    public Map<String, String> givenValidParamsAreSuppliedForGetLogonApi() {
        HashMap<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("userId", "");
        queryParamMap.put("emailAddress", "email@example.net");
        queryParamMap.put("firstName", "");
        queryParamMap.put("lastName", "");
        queryParamMap.put("startTimestamp", "2021-08-21T22:19:05");
        queryParamMap.put("endTimestamp", "2021-08-23T22:20:06");
        queryParamMap.put("size", "10");
        queryParamMap.put("page", "1");
        return queryParamMap;
    }
}
