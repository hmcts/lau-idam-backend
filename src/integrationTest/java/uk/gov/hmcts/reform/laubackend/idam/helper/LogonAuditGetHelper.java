package uk.gov.hmcts.reform.laubackend.idam.helper;

import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;

@SuppressWarnings({"PMD.UseObjectForClearerAPI"})
public final class LogonAuditGetHelper {

    private static final String DEFAULT_USER_ID = "1";
    private static final String DEFAULT_EMAIL_ADDRESS = "frank.lampard@chelsea.com";
    private static final String DEFAULT_SERVICE = "my-service";
    private static final String DEFAULT_IP_ADDRESS = "3.3.4.65.7";
    private static final String DEFAULT_LOGIN_STATE = "AUTHENTICATE";
    private static final String DEFAULT_TIMESTAMP = "2021-08-23T22:20:05.023Z";

    private LogonAuditGetHelper() {
    }

    public static LogonLogPostRequest getLogonLogPostRequest(final String userId,
                                                                                     final String emailAddress,
                                                                                     final String service,
                                                                                     final String ipAddress,
                                                                                     final String loginState,
                                                                                     final String timestamp) {
        final LogonLog logonLog = new LogonLog(userId == null ? DEFAULT_USER_ID : userId,
                                               emailAddress == null ? DEFAULT_EMAIL_ADDRESS : emailAddress,
                                               service == null ? DEFAULT_SERVICE : service,
                                               ipAddress == null ? DEFAULT_IP_ADDRESS : ipAddress,
                                               loginState == null ? DEFAULT_LOGIN_STATE : loginState,
                                               timestamp == null ? DEFAULT_TIMESTAMP : timestamp);

        return new LogonLogPostRequest(logonLog);
    }
}
