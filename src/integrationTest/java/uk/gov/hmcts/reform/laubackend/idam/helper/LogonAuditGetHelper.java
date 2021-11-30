package uk.gov.hmcts.reform.laubackend.idam.helper;

import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;

@SuppressWarnings({"PMD.UseObjectForClearerAPI"})
public final class LogonAuditGetHelper {

    private LogonAuditGetHelper() {
    }

    public static LogonLogPostRequest getLogonLogPostRequest(final String userId,
                                                                                     final String emailAddress,
                                                                                     final String service,
                                                                                     final String ipAddress,
                                                                                     final String timestamp) {
        final LogonLog logonLog = new LogonLog(userId == null ? "1" : userId,
                                               emailAddress == null ? "test@email.com" : emailAddress,
                                               service == null ? "probate" : service,
                                               ipAddress == null ? "192.168.0.1" : ipAddress,
                                               timestamp == null ? "2021-08-23T22:20:05.023Z" : timestamp);

        return new LogonLogPostRequest(logonLog);
    }
}
