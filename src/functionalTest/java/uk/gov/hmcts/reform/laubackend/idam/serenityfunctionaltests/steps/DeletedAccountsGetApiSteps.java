package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import net.serenitybdd.annotations.Step;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountsRequest;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountSearch;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeletedAccountsGetApiSteps extends BaseSteps {

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

    @Step("When valid params are supplied for Get Logon API")
    public Map<String, String> givenValidParamsAreSuppliedForGetLogonApi() {
        Map<String, String> queryParamMap = new ConcurrentHashMap<>();
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

    @Step("When valid params are supplied for Get All deleted Users API")
    public Map<String, String> givenValidParamsAreSuppliedForGetAllDeletedUsersApi() {
        Map<String, String> queryParamMap = new ConcurrentHashMap<>();
        queryParamMap.put("size", "10");
        queryParamMap.put("sort", "asc");
        return queryParamMap;
    }

    @Step("When invalid params are supplied for Get All deleted Users API")
    public Map<String, String> givenInvalidParamsAreSuppliedForGetAllDeletedUsersApi() {
        Map<String, String> queryParamMap = new ConcurrentHashMap<>();
        queryParamMap.put("sort", "asc");
        return queryParamMap;
    }
}
