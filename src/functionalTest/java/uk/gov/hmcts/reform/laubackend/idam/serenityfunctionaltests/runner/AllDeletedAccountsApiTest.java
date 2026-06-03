package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.runner;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.DeletedAccountsGetApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SerenityJUnit5Extension.class)
class AllDeletedAccountsApiTest {

    @Steps
    DeletedAccountsGetApiSteps getApiSteps;

    @Test
    @Title("Assert response code of 200 for successfully getting getAllDeletedAccount records")
    void assertHttpSuccessResponseCodeForGetRequestAllDeletedAccountsApi() throws JSONException {
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
        assertEquals(
            TestConstants.SUCCESS,
            successOrFailure,
            "GetAllDeletedAccounts Get API response code 200 is not successful"
        );
    }

    @Test
    @Title("Assert response code unauthorized request without authentication token")
    void assertUnauthorizedRequestForGetAllDeletedAccounts() {

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
        assertEquals(
            TestConstants.SUCCESS,
            successOrFailure,
            "GetAllDeletedAccounts GET API response code 401 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code forbidden without s2s authentication token")
    void assertHttpForbiddenWithInvalidS2STokenForGetAllDeletedAccounts() throws JSONException {
        String authToken = getApiSteps.validAuthorizationTokenIsGenerated();
        Response response = getApiSteps.performGetOperation(
            TestConstants.ALL_DELETED_ACCOUNTS_ENDPOINT,
            null,
            null,
            "serviceToken",
            authToken
        );

        String successOrFailure = getApiSteps.thenBadResponseIsReturned(response, 403);
        assertEquals(
            TestConstants.SUCCESS,
            successOrFailure,
            "GetAllDeletedAccounts GET API response code 403 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code bad request without size for getAllDeletedAccount")
    void assertHttpBadRequestAllDeletedAccountWithoutSize() throws JSONException {
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
        assertEquals(
            TestConstants.SUCCESS,
            successOrFailure,
            "GetAllDeletedAccounts GET API response code 400 assertion is not successful"
        );
    }
}
