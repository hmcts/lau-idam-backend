package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.runner;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.Assert;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogOnGetResponseVO;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogOnRequestVO;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.LogOnGetApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.LogOnPostApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

import java.text.ParseException;
import java.util.Map;

import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper.DatabaseCleaner.deleteLogonRecord;

@RunWith(SerenityRunner.class)
public class LogOnApiTest {


    @Steps
    LogOnPostApiSteps logOnPostApiSteps;
    @Steps
    LogOnGetApiSteps logOnGetApiSteps;

    @Test
    @Title("Assert response code of 201 for POST Request LogonApi")
    public void assertHttpSuccessResponseCodeForPostRequestCaseViewApi()
            throws JsonProcessingException, JSONException {

        String authServiceToken = logOnGetApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.FRONTEND_SERVICE_NAME);
        LogOnRequestVO logOnRequestVO = logOnPostApiSteps.generateLogOnPostRequestBody();
        Response response = logOnPostApiSteps.whenThePostServiceIsInvoked(
            TestConstants.LOGON_ENDPOINT,
            authServiceToken,
            logOnRequestVO
        );
        String successOrFailure = logOnGetApiSteps.thenASuccessResposeIsReturned(response);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "Logon POST API response code 201 assertion is not successful"
        );
        //Delete DB record
        deleteLogonRecord(response);
    }

    @Test
    @Title("Assert response code of 400 for Invalid POST request body for LogonApi")
    public void assertHttpBadResponseCodeForInvalidPostRequestBodyLogonApi()
        throws JsonProcessingException {

        String authServiceToken = logOnGetApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.FRONTEND_SERVICE_NAME);
        LogOnRequestVO logOnRequestVO = logOnPostApiSteps.generateInvalidLogonPostRequestBody();
        Response response = logOnPostApiSteps.whenThePostServiceIsInvoked(
            TestConstants.LOGON_ENDPOINT,
            authServiceToken,
            logOnRequestVO
        );
        String successOrFailure = logOnGetApiSteps.thenBadResponseIsReturned(response, 400);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "Logon POST API response code 400 assertion is not successful"
        );
    }

    @Test
    @Title("Assert response code of 200 for GET LogonApi with valid headers and valid request params")
    public void assertHttpSuccessResponseCodeForCaseViewApi() throws JsonProcessingException, ParseException,
        JSONException {

        String authServiceToken = logOnGetApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.FRONTEND_SERVICE_NAME);
        final String authorizationToken = logOnGetApiSteps.validAuthorizationTokenIsGenerated();
        LogOnRequestVO logOnRequestVO = logOnPostApiSteps.generateLogOnPostRequestBody();
        final Response postResponse = logOnPostApiSteps.whenThePostServiceIsInvoked(
            TestConstants.LOGON_ENDPOINT,
            authServiceToken,
            logOnRequestVO
        );
        Map<String, String> queryParamMap = logOnGetApiSteps.givenValidParamsAreSuppliedForGetLogonApi();
        Response response = logOnGetApiSteps.whenTheGetLogonServiceIsInvokedWithTheGivenParams(
            authServiceToken,
            authorizationToken,
            queryParamMap
        );
        ObjectMapper objectMapper = new ObjectMapper();
        LogOnGetResponseVO logOnGetResponseVO = objectMapper.readValue(
            response.getBody().asString(),
            LogOnGetResponseVO.class
        );

        logOnGetApiSteps.thenASuccessResposeIsReturned(response);
        logOnGetApiSteps.thenAtLeastOneRecordNumberShouldExist(response);
        logOnGetApiSteps.thenTheGetLogonResponseParamsMatchesTheInput(queryParamMap, logOnGetResponseVO);
        String successOrFailure = logOnGetApiSteps.thenTheGetLogonResponseDateRangeMatchesTheInput(
            queryParamMap,
            logOnGetResponseVO
        );
        Assert.assertEquals(successOrFailure, TestConstants.SUCCESS,
                            "The assertion for GET Logon API response code 200 is not successful"
        );
        //Delete DB record
        deleteLogonRecord(postResponse);
    }

    @SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
    @Test
    @Title("Assert response code of 400 for GET LogonApi with Empty Params")
    public void assertResponseCodeOf400WithInvalidParamsForLogonApi() throws JSONException {
        String authServiceToken = logOnGetApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.FRONTEND_SERVICE_NAME);
        final String authorizationToken = logOnGetApiSteps.validAuthorizationTokenIsGenerated();
        Map<String, String> queryParamMap = logOnGetApiSteps.givenEmptyParamsAreSuppliedForGetLogon();
        Response response = logOnGetApiSteps.whenTheGetLogonServiceIsInvokedWithTheGivenParams(
            authServiceToken,
            authorizationToken,
            queryParamMap
        );
        String successOrFailure = logOnGetApiSteps.thenBadResponseIsReturned(response, 400);
        Assert.assertEquals(successOrFailure, TestConstants.SUCCESS, "The assertion is not successful");
    }

    @Test
    @Title("Assert response code of 403 for GET LogonApi with missing ServiceAuthorization token")
    public void assertResponseCodeOf403ForMissingS2SForLogonApi() throws JSONException {
        final String authorizationToken = logOnGetApiSteps.validAuthorizationTokenIsGenerated();
        Map<String, String> queryParamMap = logOnGetApiSteps.givenValidParamsAreSuppliedForGetLogonApi();
        Response response = logOnGetApiSteps.whenTheGetLogonServiceIsInvokedWithTheGivenParams(
            "",
            authorizationToken,
            queryParamMap
        );
        String successOrFailure = logOnGetApiSteps.thenBadResponseIsReturned(response, 403);
        Assert.assertEquals(successOrFailure, TestConstants.SUCCESS, "The assertion is not successful");
    }

    @Test
    @Title("Assert response code of 401 for GET LogonApi with missing User Authorization token")
    public void assertResponseCodeOf401ForMissingAuthorizationTokenForLogonApi() {
        String authServiceToken = logOnGetApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.FRONTEND_SERVICE_NAME);
        Map<String, String> queryParamMap = logOnGetApiSteps.givenValidParamsAreSuppliedForGetLogonApi();
        Response response = logOnGetApiSteps.whenTheGetLogonServiceIsInvokedWithTheGivenParams(
            authServiceToken,
            "",
            queryParamMap
        );
        String successOrFailure = logOnGetApiSteps.thenBadResponseIsReturned(response, 401);
        Assert.assertEquals(successOrFailure, TestConstants.SUCCESS, "The assertion is not successful");
    }

    @Test
    @Title("Assert response code of 401 for GET LogonApi with invalid User Authorization token")
    public void assertResponseCodeOf401ForInvalidAuthorizationTokenForLogonApi() throws JSONException {
        String authServiceToken = logOnGetApiSteps.givenAValidServiceTokenIsGenerated(
            TestConstants.FRONTEND_SERVICE_NAME);
        final String authorizationToken = logOnGetApiSteps.givenTheInvalidAuthorizationTokenIsGenerated();
        Map<String, String> queryParamMap = logOnGetApiSteps.givenValidParamsAreSuppliedForGetLogonApi();
        Response response = logOnGetApiSteps.whenTheGetLogonServiceIsInvokedWithTheGivenParams(
            authServiceToken,
            authorizationToken,
            queryParamMap
        );
        String successOrFailure = logOnGetApiSteps.thenBadResponseIsReturned(response, 401);
        Assert.assertEquals(successOrFailure, TestConstants.SUCCESS, "The assertion is not successful");
    }

}
