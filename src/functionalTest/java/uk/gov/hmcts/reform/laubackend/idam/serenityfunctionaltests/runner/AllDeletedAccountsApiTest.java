package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.runner;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.Assert;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.DeletedAccountsGetApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

import java.util.Map;

@RunWith(SerenityRunner.class)
public class AllDeletedAccountsApiTest {

    @Steps
    DeletedAccountsGetApiSteps getApiSteps;

    @Test
    @Title("Assert response code of 200 for successfully getting getAllDeletedAccount records")
    public void assertHttpSuccessResponseCodeForGetRequestAllDeletedAccountsApi() throws JSONException {
        String serviceToken = getApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.DISPOSER_USER_SERVICE_NAME);

        String authToken = getApiSteps.validAuthorizationTokenIsGenerated();
        Map<String, String> queryParams = getApiSteps.givenValidParamsAreSuppliedForGetAllDeletedUsersApi();

        Response response = getApiSteps.performGetOperation(
            TestConstants.ALL_DELETED_ACCOUNTS_ENDPOINT,
            null,
            queryParams,
            serviceToken,
            authToken
        );

        String successOrFailure = getApiSteps.thenASuccessResposeIsReturned(response);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "GetAllDeletedAccounts Get API response code 200 is not successful"
        );
    }

    @Test
    @Title("Assert response code unauthorized request without authentication token")
    public void assertUnauthorizedRequestForGetAllDeletedAccounts() {

        String serviceToken = getApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.DISPOSER_USER_SERVICE_NAME);

        Response response = getApiSteps.performGetOperation(
            TestConstants.ALL_DELETED_ACCOUNTS_ENDPOINT,
            null,
            null,
            serviceToken,
            "authToken"
        );
        String successOrFailure = getApiSteps.thenBadResponseIsReturned(response, 401);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "GetAllDeletedAccounts GET API response code 401 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code forbidden without s2s authentication token")
    public void assertHttpForbiddenWithInvalidS2STokenForGetAllDeletedAccounts() {

        Response response = getApiSteps.performGetOperation(
            TestConstants.ALL_DELETED_ACCOUNTS_ENDPOINT,
            null,
            null,
            "serviceToken",
            "Bearer something"
        );

        String successOrFailure = getApiSteps.thenBadResponseIsReturned(response, 403);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "GetAllDeletedAccounts GET API response code 403 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code bad request without size for getAllDeletedAccount")
    public void assertHttpBadRequestAllDeletedAccountWithoutSize() throws JSONException {
        String serviceToken = getApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.DISPOSER_USER_SERVICE_NAME);
        String authToken = getApiSteps.validAuthorizationTokenIsGenerated();
        Map<String, String> queryParams = getApiSteps.givenInvalidParamsAreSuppliedForGetAllDeletedUsersApi();

        Response response = getApiSteps.performGetOperation(
            TestConstants.ALL_DELETED_ACCOUNTS_ENDPOINT,
            null,
            queryParams,
            serviceToken,
            authToken
        );
        String successOrFailure = getApiSteps.thenBadResponseIsReturned(response, 400);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "GetAllDeletedAccounts GET API response code 400 assertion is not successful"
        );
    }
}
