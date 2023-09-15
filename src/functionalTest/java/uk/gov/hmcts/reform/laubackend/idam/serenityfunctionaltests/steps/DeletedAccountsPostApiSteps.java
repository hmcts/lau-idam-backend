package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import net.serenitybdd.annotations.Step;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccount;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountsRequest;

import java.util.Arrays;

public class DeletedAccountsPostApiSteps extends BaseSteps {

    @Step("Given the POST service body is generated")
    public DeletedAccountsRequest generateDeletedAccountsRequest() {
        DeletedAccount deletedAccount = DeletedAccount.builder()
            .userId("user id")
            .emailAddress("email@example.net")
            .firstName("John")
            .lastName("Smith")
            .deletionTimestamp("2023-05-23T09:34:23.432Z")
            .build();

        DeletedAccountsRequest request = new DeletedAccountsRequest();
        request.setDeletionLogs(Arrays.asList(deletedAccount));
        return request;
    }

    @Step("Given invalid POST body is generated")
    public DeletedAccountsRequest generateInvalidDeletedAccountsRequest() {
        DeletedAccount deletedAccount = DeletedAccount.builder()
            .userId("user id")
            .emailAddress("")
            .firstName("John")
            .lastName("Smith")
            .deletionTimestamp("2023-05-23T09:34:23.432Z")
            .build();

        DeletedAccountsRequest request = new DeletedAccountsRequest();
        request.setDeletionLogs(Arrays.asList(deletedAccount));
        return request;
    }
}
