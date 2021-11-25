package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.runner;


import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.Assert;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogOnRequestVO;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.LogOnGetApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps.LogOnPostApiSteps;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

@RunWith(SerenityRunner.class)
public class LogOnApiTest {


    @Steps
    LogOnPostApiSteps logOnPostApiSteps;
    @Steps
    LogOnGetApiSteps logOnGetApiSteps;

    @Test
    @Title("Assert response code of 201 for POST Request LogonApi")
    public void assertHttpSuccessResponseCodeForPostRequestCaseViewApi()
        throws com.fasterxml.jackson.core.JsonProcessingException {

        String authServiceToken = logOnGetApiSteps.givenAValidServiceTokenIsGenerated();
        LogOnRequestVO logOnRequestVO = logOnPostApiSteps.generateLogOnPostRequestBody();
        Response response = logOnPostApiSteps.whenThePostServiceIsInvoked(authServiceToken, logOnRequestVO);
        String successOrFailure = logOnGetApiSteps.thenASuccessResposeIsReturned(response);
        Assert.assertEquals(
            successOrFailure,
            TestConstants.SUCCESS,
            "Logon POST API response code 201 assertion is not successful"
        );
    }

}
