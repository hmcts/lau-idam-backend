package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import net.serenitybdd.annotations.Step;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogOnRequestVO;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogonLog;


public class LogOnPostApiSteps extends BaseSteps {

    @SuppressWarnings({"PMD.AvoidUsingHardCodedIP"})
    @Step("Given the POST service body is generated")
    public LogOnRequestVO generateLogOnPostRequestBody() {
        LogonLog logonLog = new LogonLog();
        logonLog.setId("1");
        logonLog.setUserId("3734555");
        logonLog.setEmailAddress("firstname.lastname@company.com");
        logonLog.setService("idam-web-admin");
        logonLog.setIpAddress("192.158.1.38");
        logonLog.setLoginState("AUTHORIZE");
        logonLog.setTimestamp("2021-08-23T22:20:05.023Z");
        LogOnRequestVO logOnRequestVO = new LogOnRequestVO();
        logOnRequestVO.setLogonLog(logonLog);
        return logOnRequestVO;
    }

    @Step("Given invalid POST service body is generated")
    public LogOnRequestVO generateInvalidLogonPostRequestBody() {
        LogonLog logonLog = new LogonLog();
        logonLog.setId("1");
        logonLog.setUserId("37@@@");
        logonLog.setEmailAddress("firs@");
        logonLog.setService("xxx");
        logonLog.setIpAddress("12");
        logonLog.setTimestamp("2021-08-23T22:20:05.023");
        LogOnRequestVO logOnRequestVO = new LogOnRequestVO();
        logOnRequestVO.setLogonLog(logonLog);
        return logOnRequestVO;
    }
}
