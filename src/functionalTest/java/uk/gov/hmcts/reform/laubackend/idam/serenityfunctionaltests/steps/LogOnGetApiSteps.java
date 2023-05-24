package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import io.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogOnGetResponseVO;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings({"PMD.TooManyMethods", "PMD.CyclomaticComplexity"})
public class LogOnGetApiSteps extends BaseSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogOnGetApiSteps.class);

    @Step("And valid Authorization token is generated")
    public String validAuthorizationTokenIsGenerated() throws JSONException {
        return authorizationHeaderHelper.getAuthorizationToken();
    }

    @SuppressWarnings({"PMD.AvoidUsingHardCodedIP"})
    @Step("When valid params are supplied for Get Logon API")
    public Map<String, String> givenValidParamsAreSuppliedForGetLogonApi() {
        HashMap<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("userId", "3734555");
        queryParamMap.put("emailAddress", "firstname.lastname@company.com");
        queryParamMap.put("service", "idam-web-admin");
        queryParamMap.put("ipAddress", "192.158.1.38");
        queryParamMap.put("startTimestamp", "2021-08-21T22:19:05");
        queryParamMap.put("endTimestamp", "2021-08-23T22:20:06");
        return queryParamMap;
    }

    @Step("When the Logon GET service is invoked with the valid params")
    public Response whenTheGetLogonServiceIsInvokedWithTheGivenParams(String serviceToken,
                                                                      String authorizationToken,
                                                                      Map<String, String> queryParamMap) {
        return performGetOperation(TestConstants.LOGON_ENDPOINT,
                                   null, queryParamMap, serviceToken,authorizationToken
        );
    }

    @Step("Then at least one record number should exist")
    public void thenAtLeastOneRecordNumberShouldExist(Response response) {
        response.then().assertThat().body("startRecordNumber", Matchers.is(Matchers.greaterThanOrEqualTo(1)));

    }

    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis"})
    @Step("Then the GET Logon response params match the input")
    public String thenTheGetLogonResponseParamsMatchesTheInput(Map<String, String> inputQueryParamMap,
                                                                    LogOnGetResponseVO logOnGetResponseVO) {
        int startRecordNumber = logOnGetResponseVO.getStartRecordNumber();
        Assert.assertTrue("Check if the the record number is greater than )",startRecordNumber > 0);
        List<LogonLog> logonLogList = logOnGetResponseVO.getLogonLog();
        LogonLog logonLogObj = logonLogList == null || logonLogList.get(0) == null
            ? new LogonLog() : logonLogList.get(0);
        for (String queryParam : inputQueryParamMap.keySet()) {

            if ("userId".equals(queryParam)) {
                String userId = logonLogObj.getUserId();
                Assert.assertEquals(
                    "User Id is missing in the response",
                    inputQueryParamMap.get(queryParam), userId
                );
            } else if ("emailAddress".equals(queryParam)) {
                String emailAddress = logonLogObj.getEmailAddress();
                Assert.assertEquals(
                    "emailAddress is missing in the response",
                    inputQueryParamMap.get(queryParam), emailAddress
                );

            } else if ("service".equals(queryParam)) {
                String service = logonLogObj.getService();
                Assert.assertEquals(
                    "service is missing in the response",
                    inputQueryParamMap.get(queryParam), service
                );

            } else if ("ipAddress".equals(queryParam)) {
                String ipAddress = logonLogObj.getIpAddress();
                Assert.assertEquals(
                    "ipAddress is missing in the response",
                    inputQueryParamMap.get(queryParam), ipAddress
                );
            } else if ("loginState".equals(queryParam)) {
                String loginState = logonLogObj.getLoginState();
                Assert.assertEquals(
                        "loginState is missing in the response",
                        inputQueryParamMap.get(queryParam), loginState
                );
            } else if ("timestamp".equals(queryParam)) {
                String timestamp = logonLogObj.getTimestamp();
                Assert.assertEquals(
                    "timestamp is missing in the response",
                    inputQueryParamMap.get(queryParam), timestamp
                );
            }
        }
        return TestConstants.SUCCESS;
    }

    @Step("Then the GET Logon response date range matches the input")
    public String thenTheGetLogonResponseDateRangeMatchesTheInput(Map<String, String> inputQueryParamMap,
                                                                     LogOnGetResponseVO logOnGetResponseVO)
        throws ParseException {
        List<LogonLog> logonLogList = logOnGetResponseVO.getLogonLog();
        LogonLog logOnLogObject = logonLogList.get(0);
        String timeStampResponse = logOnLogObject.getTimestamp();
        String timeStampStartInputParam = inputQueryParamMap.get("startTimestamp");
        String timeStampEndInputParam = inputQueryParamMap.get("endTimestamp");

        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
        String responseDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        Date inputStartTimestamp = new SimpleDateFormat(dateFormat, Locale.UK).parse(timeStampStartInputParam);
        Date inputEndTimestamp = new SimpleDateFormat(dateFormat, Locale.UK).parse(timeStampEndInputParam);
        Date responseTimestamp = new SimpleDateFormat(responseDateFormat, Locale.UK).parse(timeStampResponse);

        LOGGER.info("Input start date : " + inputStartTimestamp.getTime());
        LOGGER.info("Input end date : " + inputEndTimestamp.getTime());
        LOGGER.info("Output date : " + responseTimestamp.getTime());

        Assert.assertTrue("Verify record response timestamp",
                responseTimestamp.after(inputStartTimestamp) && responseTimestamp.before(
            inputEndTimestamp) || responseTimestamp.getTime() == inputStartTimestamp.getTime()
                              || responseTimestamp.getTime() == inputEndTimestamp.getTime()
        );
        return TestConstants.SUCCESS;
    }

    @Step("Given empty params values are supplied for the GET Logon API")
    public Map<String, String> givenEmptyParamsAreSuppliedForGetLogon() {
        Map<String, String> queryParamMap = new ConcurrentHashMap<>();
        queryParamMap.put("userId", "");
        queryParamMap.put("emailAddress", "");
        queryParamMap.put("service", "");
        queryParamMap.put("ipAddress", "");
        queryParamMap.put("startTimestamp", "");
        queryParamMap.put("endTimestamp", "");
        return queryParamMap;
    }

    @Step("Given the invalid authorization token is generated")
    public String givenTheInvalidAuthorizationTokenIsGenerated() throws JSONException {
        String authServiceToken = validAuthorizationTokenIsGenerated();
        return authServiceToken + "abc";
    }

}
