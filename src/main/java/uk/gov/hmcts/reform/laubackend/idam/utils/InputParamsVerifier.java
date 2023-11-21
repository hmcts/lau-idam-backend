package uk.gov.hmcts.reform.laubackend.idam.utils;

import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogAllUsersRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import java.util.List;

import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.EMAIL_ADDRESS_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.FIRSTNAME_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.FIRSTNAME_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.IPADDRESS_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.LASTNAME_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.LASTNAME_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.LOGIN_STATE_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.PAGE_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.PAGE_SIZE_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.SERVICE_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.SORT_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.TIMESTAMP_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.TIMESTAMP_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.USERID_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.USERID_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.RegexConstants.TIMESTAMP_GET_REGEX;
import static uk.gov.hmcts.reform.laubackend.idam.constants.RegexConstants.TIMESTAMP_POST_REGEX;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyEmailAddress;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyIpAddress;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyLength;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyLoginState;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyPositiveNumeric;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyPositiveNumericAndNotNull;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyService;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifySort;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyTimestamp;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyUserId;

@SuppressWarnings({"PMD.ExcessiveImports"})
public final class InputParamsVerifier {

    private static final int MAX_NAME_POST_LENGTH = 64;
    private static final int MAX_NAME_GET_LENGTH = 70;

    private InputParamsVerifier() {
    }

    public static void verifyRequestLogonLogParamsConditions(final LogonLog logonLog)
            throws InvalidRequestException {
        verifyUserId(logonLog.getUserId(), USERID_POST_EXCEPTION_MESSAGE);
        verifyService(logonLog.getService(), SERVICE_POST_EXCEPTION_MESSAGE);
        verifyEmailAddress(logonLog.getEmailAddress(), EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE);
        verifyIpAddress(logonLog.getIpAddress(), IPADDRESS_POST_EXCEPTION_MESSAGE);
        verifyTimestamp(logonLog.getTimestamp(), TIMESTAMP_POST_EXCEPTION_MESSAGE, TIMESTAMP_POST_REGEX);
        verifyLoginState(logonLog.getLoginState(), LOGIN_STATE_EXCEPTION_MESSAGE);
    }

    public static void verifyRequestLogonParamsConditions(final LogonInputParamsHolder inputParamsHolder)
        throws InvalidRequestException {
        verifyUserId(inputParamsHolder.getUserId(), USERID_GET_EXCEPTION_MESSAGE);
        verifyEmailAddress(inputParamsHolder.getEmailAddress(), EMAIL_ADDRESS_GET_EXCEPTION_MESSAGE);
        verifyTimestamp(inputParamsHolder.getStartTime(), TIMESTAMP_GET_EXCEPTION_MESSAGE, TIMESTAMP_GET_REGEX);
        verifyTimestamp(inputParamsHolder.getEndTime(), TIMESTAMP_GET_EXCEPTION_MESSAGE, TIMESTAMP_GET_REGEX);
    }

    public static void verifyUserDeletionPostRequestParams(
        final List<DeletionLog> deletionLogs
    ) throws InvalidRequestException {
        for (DeletionLog log: deletionLogs) {
            verifyUserId(log.getUserId(), USERID_POST_EXCEPTION_MESSAGE);
            verifyEmailAddress(log.getEmailAddress(), EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE);
            verifyLength(log.getFirstName(), MAX_NAME_POST_LENGTH, FIRSTNAME_POST_EXCEPTION_MESSAGE);
            verifyLength(log.getLastName(), MAX_NAME_POST_LENGTH, LASTNAME_POST_EXCEPTION_MESSAGE);
            verifyTimestamp(log.getDeletionTimestamp(), TIMESTAMP_POST_EXCEPTION_MESSAGE, TIMESTAMP_POST_REGEX);
        }
    }

    public static void verifyUserDeletionGetRequestParams(
        final DeletionLogGetRequestParams params
    ) throws InvalidRequestException {
        verifyUserId(params.userId(), USERID_GET_EXCEPTION_MESSAGE);
        verifyEmailAddress(params.emailAddress(), EMAIL_ADDRESS_GET_EXCEPTION_MESSAGE);
        verifyLength(params.firstName(), MAX_NAME_GET_LENGTH, FIRSTNAME_GET_EXCEPTION_MESSAGE);
        verifyLength(params.lastName(), MAX_NAME_GET_LENGTH, LASTNAME_GET_EXCEPTION_MESSAGE);
        verifyTimestamp(params.startTimestamp(), TIMESTAMP_GET_EXCEPTION_MESSAGE, TIMESTAMP_GET_REGEX);
        verifyTimestamp(params.endTimestamp(), TIMESTAMP_GET_EXCEPTION_MESSAGE, TIMESTAMP_GET_REGEX);
        verifyPositiveNumeric(params.size(), PAGE_SIZE_EXCEPTION_MESSAGE);
        verifyPositiveNumeric(params.page(), PAGE_EXCEPTION_MESSAGE);
    }

    public static void verifyAllUserDeletionGetRequestParams(
        final DeletionLogAllUsersRequestParams params
    ) throws InvalidRequestException {
        verifyPositiveNumericAndNotNull(params.size(), PAGE_SIZE_EXCEPTION_MESSAGE);
        verifySort(params.sort(), SORT_EXCEPTION_MESSAGE);
    }
}
