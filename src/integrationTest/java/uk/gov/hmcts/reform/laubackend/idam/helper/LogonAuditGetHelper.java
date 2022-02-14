package uk.gov.hmcts.reform.laubackend.idam.helper;

import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;

@SuppressWarnings({"PMD.UseObjectForClearerAPI","PMD.AvoidUsingHardCodedIP"})
public final class LogonAuditGetHelper {

    private static final String DEFAULT_USER_ID = "1";
    private static final String DEFAULT_EMAIL_ADDRESS = "frank.lampard@chelsea.com";
    //"ww0EBwMCA94Iql0fa9Jt0koBThRiicFKIVOLJGVlAMeUmeXfSpHay4U50v8xhoDaxaghIdHRIo9w\n"
    //    + "EmzP5Ht+UiN3y4BIioSgrs3B8dJIFpZ3DT+yfYauuQ4ggA=="; //frank.lampard@chelsea.com
    private static final String DEFAULT_SERVICE = "my-service";
    private static final String DEFAULT_IP_ADDRESS = "3.3.4.65.7";
    //"ww0EBwMCMpcp/Mz4UXdz0jsBQ2Mh6SfKK1H9JTUinEcYL7aZd5HCET/QCFumYW6tpO5Gh6AeaqeB\n"
    //    + "lgV4Q5cwhAahFMyq7381QHu/BQ=="; //3.3.4.65.7
    private static final String DEFAULT_TIMESTAMP = "2021-08-23T22:20:05.023Z";

    private LogonAuditGetHelper() {
    }

    public static LogonLogPostRequest getLogonLogPostRequest(final String userId,
                                                                                     final String emailAddress,
                                                                                     final String service,
                                                                                     final String ipAddress,
                                                                                     final String timestamp) {
        final LogonLog logonLog = new LogonLog(userId == null ? DEFAULT_USER_ID : userId,
                                               emailAddress == null ? DEFAULT_EMAIL_ADDRESS : emailAddress,
                                               service == null ? DEFAULT_SERVICE : service,
                                               ipAddress == null ? DEFAULT_IP_ADDRESS : ipAddress,
                                               timestamp == null ? DEFAULT_TIMESTAMP : timestamp);

        return new LogonLogPostRequest(logonLog);
    }
}
