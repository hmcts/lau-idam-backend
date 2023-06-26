package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.Assert;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountsRequest;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.DeletedAccountsGetApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.DeletedAccountsPostApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

import java.util.HashMap;
import java.util.Map;


@RunWith(SerenityRunner.class)
public class DeletedAccountsApiTest {

    @Steps
    DeletedAccountsPostApiSteps postApiSteps;

    @Steps
    DeletedAccountsGetApiSteps getApiSteps;

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

    @Test
    @Title("Assert response code of 201 for GET Request deletedAccounts")
    public void assertHttpSuccessResponseCodeForGetRequestCaseViewApi()
        throws JsonProcessingException, JSONException {

        String serviceToken = getApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.USER_DISPOSER_SERVICE_NAME);

        String authToken = getApiSteps.validAuthorizationTokenIsGenerated();
        Map<String, String> queryParams = getApiSteps.givenValidParamsAreSuppliedForGetLogonApi();

        Response response = getApiSteps.whenTheGetServiceIsInvoked(
            TestConstants.DELETED_ACCOUNTS_ENDPOINT,
            serviceToken,
            authToken,
            queryParams
        );


        String successOrFailure = getApiSteps.thenASuccessResposeIsReturned(response);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "DeletedAccounts POST API response code 201 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code bad request without start timestamp")
    public void assertHttpBadRequestWithoutStartTimestamp() throws JsonProcessingException {

        String serviceToken = getApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.USER_DISPOSER_SERVICE_NAME);
        DeletedAccountsRequest request = getApiSteps.generateInvalidDeletedAccountsSearchRequest();

        Response response = getApiSteps.whenThePostServiceIsInvoked(
            TestConstants.DELETED_ACCOUNTS_ENDPOINT,
            serviceToken,
            request
        );
        String successOrFailure = getApiSteps.thenBadResponseIsReturned(response, 400);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "DeletedAccountsSearch GET API response code 400 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code bad request without start timestamp")
    public void assertUnauthorizedRequest() throws JsonProcessingException, JSONException {

        String serviceToken = getApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.USER_DISPOSER_SERVICE_NAME);

        Map<String, String> header = new HashMap<>();

        Response response = getApiSteps.whenTheGetServiceIsInvoked(
            TestConstants.DELETED_ACCOUNTS_ENDPOINT,
            serviceToken,
            "authToken",
            null
        );
        String successOrFailure = getApiSteps.thenBadResponseIsReturned(response, 401);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "DeletedAccountsSearch GET API response code 401 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code forbidden without s2s authentication token")
    public void assertGetHttpForbiddenWithInvalidS2SToken() throws JsonProcessingException {
        String serviceToken = getApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.USER_DISPOSER_SERVICE_NAME);

        Response response = getApiSteps.whenTheGetServiceIsInvoked(
            TestConstants.DELETED_ACCOUNTS_ENDPOINT,
            "Bearer something",
            serviceToken,
            null
        );

        String successOrFailure = getApiSteps.thenBadResponseIsReturned(response, 403);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "DeletedAccountsSearch GET API response code 403 assertion is not successful"
        );
    }
}
