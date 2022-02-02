
package uk.gov.hmcts.reform.laubackend.idam.utils;

import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public final class NotEmptyInputParamsVerifier {

    private NotEmptyInputParamsVerifier() {
    }

    public static void verifyLogonLogRequestAreNotEmpty(final LogonLog logonLog)
            throws InvalidRequestException {
        if (isEmpty(logonLog.getUserId())
                || isEmpty(logonLog.getEmailAddress())
                || isEmpty(logonLog.getService())
                || isEmpty(logonLog.getTimestamp())) {
            throw new InvalidRequestException("You need to populate all required parameters - "
                    + "userId, email, service and timestamp ",
                    BAD_REQUEST);
        }
    }

    public static void verifyRequestLogonParamsAreNotEmpty(final LogonInputParamsHolder inputParamsHolder)
        throws InvalidRequestException {
        if (isEmpty(inputParamsHolder.getUserId())
            && isEmpty(inputParamsHolder.getEmailAddress())
            && isEmpty(inputParamsHolder.getStartTime())
            && isEmpty(inputParamsHolder.getEndTime())) {
            throw new InvalidRequestException("At least one path parameter must be present", BAD_REQUEST);
        }
    }
}

