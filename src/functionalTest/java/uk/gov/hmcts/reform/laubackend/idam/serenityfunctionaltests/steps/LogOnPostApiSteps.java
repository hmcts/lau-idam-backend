package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogOnRequestVO;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

public class LogOnPostApiSteps extends BaseSteps {

    @SuppressWarnings({"PMD.AvoidUsingHardCodedIP"})
    @Step("Given the POST service body is generated")
    public LogOnRequestVO generateLogOnPostRequestBody() {
        LogonLog logonLog = new LogonLog();
        logonLog.setId("1");
        logonLog.setUserId("3734574");
        logonLog.setEmailAddress("firstname.lastname@company.com");
        logonLog.setService("idam-web-admin");
        logonLog.setIpAddress("192.158.1.38");
        logonLog.setTimestamp("2021-08-23T22:20:05.023Z");
        LogOnRequestVO logOnRequestVO = new LogOnRequestVO();
        logOnRequestVO.setLogonLog(logonLog);
        return logOnRequestVO;
    }

    @Step("When the POST service is invoked")
    public Response whenThePostServiceIsInvoked(String serviceToken, Object logonLog) throws JsonProcessingException {
        return performPostOperation(TestConstants.LOGON_ENDPOINT, null, null, logonLog, serviceToken);
    }
}
