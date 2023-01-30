package uk.gov.hmcts.reform.laubackend.idam.utils;

import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.EMAIL_ADDRESS_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.IPADDRESS_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.LOGIN_STATE_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.SERVICE_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.TIMESTAMP_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.TIMESTAMP_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.USERID_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.USERID_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.RegexConstants.TIMESTAMP_GET_REGEX;
import static uk.gov.hmcts.reform.laubackend.idam.constants.RegexConstants.TIMESTAMP_POST_REGEX;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyEmailAddress;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyIpAddress;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyLoginState;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyService;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyTimestamp;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyUserId;

public final class InputParamsVerifier {
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
}
