package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.Assert;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountsRequest;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.DeletedAccountsPostApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;


@RunWith(SerenityRunner.class)
public class DeletedAccountsApiTest {

    @Steps
    DeletedAccountsPostApiSteps postApiSteps;

    @Test
    @Title("Assert response code of 201 for POST Request deletedAccounts")
    public void assertHttpSuccessResponseCodeForPostRequestCaseViewApi()
        throws JsonProcessingException {

        String authServiceToken = postApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.USER_DISPOSER_SERVICE_NAME);
        DeletedAccountsRequest request = postApiSteps.generateDeletedAccountsRequest();

        Response response = postApiSteps.whenThePostServiceIsInvoked(
            TestConstants.DELETED_ACCOUNTS_ENDPOINT,
            authServiceToken,
            request
        );
        String successOrFailure = postApiSteps.thenASuccessResposeIsReturned(response);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "DeletedAccounts POST API response code 201 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code of 400 for Invalid POST request body for deletedAccounts")
    public void assertHttpBadResponseCodeForInvalidPostRequestBodyLogonApi()
        throws JsonProcessingException {

        String authServiceToken = postApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.USER_DISPOSER_SERVICE_NAME);
        DeletedAccountsRequest request = postApiSteps.generateInvalidDeletedAccountsRequest();
        Response response = postApiSteps.whenThePostServiceIsInvoked(
            TestConstants.DELETED_ACCOUNTS_ENDPOINT,
            authServiceToken,
            request
        );
        String successOrFailure = postApiSteps.thenBadResponseIsReturned(response, 400);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "Logon POST API response code 400 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code forbidden without s2s authentication token")
    public void assertHttpForbiddenWithInvalidS2SToken() throws JsonProcessingException {
        DeletedAccountsRequest request = postApiSteps.generateDeletedAccountsRequest();

        Response response = postApiSteps.whenThePostServiceIsInvoked(
            TestConstants.DELETED_ACCOUNTS_ENDPOINT,
            "Bearer something",
            request
        );
        String successOrFailure = postApiSteps.thenAForbiddenResposeIsReturned(response);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "DeletedAccounts POST API response code 403 assertion is not successful"
        );
    }


}
