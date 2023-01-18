package uk.gov.hmcts.reform.laubackend.idam.helper;

import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public final class LogonAuditPostHelper {

    private LogonAuditPostHelper() {
    }

    public static LogonLogPostRequest getLogonAudit() {
        final LogonLog logonLog = new LogonLog("1",
                "frank.lampard@chelsea.com",
                "my-service",
                "3.3.4.65.7",
                "AUTHENTICATE",
                "2021-08-23T22:20:05.023Z");

        return new LogonLogPostRequest(logonLog);
    }

    public static LogonLogPostRequest getLogonAudit(final String userId) {
        final LogonLog logonLog = new LogonLog(userId,
                "frank.lampard@chelsea.com",
                "my-service",
                "3.3.4.65.7",
                "AUTHENTICATE",
                "2021-08-23T22:20:05.023Z");

        return new LogonLogPostRequest(logonLog);
    }

    public static LogonLogPostRequest getLogonAuditWithMissingMandatoryBodyParameter() {
        final LogonLog logonLog = new LogonLog("1",
                null,
                "my-service",
                "3.3.4.65.7",
                "AUTHENTICATE",
                "2021-08-23T22:20:05.023Z");

        return new LogonLogPostRequest(logonLog);
    }

    public static LogonLogPostRequest getLogonAuditWithInvalidParameter() {
        final LogonLog logonLog = new LogonLog(randomAlphanumeric(65),
                "frank.lampard@chelsea.com",
                "my-service",
                "3.3.4.65.7",
                "AUTHENTICATE",
                "2021-08-23T22:20:05.023Z");

        return new LogonLogPostRequest(logonLog);
    }
}
