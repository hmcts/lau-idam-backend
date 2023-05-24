
package uk.gov.hmcts.reform.laubackend.idam.utils;

import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SuppressWarnings({"PMD.UselessParentheses"})
public final class NotEmptyInputParamsVerifier {

    private NotEmptyInputParamsVerifier() {
    }

    public static void verifyLogonLogRequestAreNotEmpty(final LogonLog logonLog)
            throws InvalidRequestException {
        if (isEmpty(logonLog.getUserId())
            || isEmpty(logonLog.getEmailAddress())
            || isEmpty(logonLog.getTimestamp())) {
            throw new InvalidRequestException(
                "You need to populate all required parameters - "
                    + "userId, email and timestamp ",
                BAD_REQUEST
            );
        }
    }

    public static void verifyRequestLogonParamsAreNotEmpty(final LogonInputParamsHolder inputParamsHolder)
        throws InvalidRequestException {
        final boolean isUserIdNotEmpty = !isEmpty(inputParamsHolder.getUserId());
        final boolean isEmailAddressNotEmpty = !isEmpty(inputParamsHolder.getEmailAddress());
        final boolean isStartTimeNotEmpty = !isEmpty(inputParamsHolder.getStartTime());
        final boolean isEndTimeNotEmpty = !isEmpty(inputParamsHolder.getEndTime());

        if (!((isStartTimeNotEmpty && isEndTimeNotEmpty)
            && (isUserIdNotEmpty || isEmailAddressNotEmpty))) {
            throw new InvalidRequestException("Both startTime and endTime must be present "
                                                  + "and at least one of the parameters ((userId, emailAddress "
                                                  + ") must not be empty", BAD_REQUEST);
        }
    }

    public static void verifyUserDeletionPostRequestParamsPresence(final List<DeletionLog> logs)
        throws InvalidRequestException {

        if (logs == null) {
            throw new InvalidRequestException("At least one deletion log is required", BAD_REQUEST);
        }

        for (DeletionLog log: logs) {
            boolean missingUserId = isEmpty(log.getUserId());
            boolean missingEmail = isEmpty(log.getEmailAddress());
            boolean missingName = isEmpty(log.getFirstName()) || isEmpty(log.getLastName());
            boolean missingTimestamp = isEmpty(log.getDeletionTimestamp());

            if (missingUserId || missingEmail || missingName || missingTimestamp) {
                throw new InvalidRequestException("All parameters for all deletion logs are required", BAD_REQUEST);
            }
        }
    }
}

