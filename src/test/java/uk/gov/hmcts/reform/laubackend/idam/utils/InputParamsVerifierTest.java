package uk.gov.hmcts.reform.laubackend.idam.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.IPADDRESS_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.SERVICE_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.TIMESTAMP_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.USERID_POST_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyRequestLogonLogParamsConditions;

@TestInstance(PER_CLASS)
class InputParamsVerifierTest {

    @Test
    void shouldNotVerifyUserIdForLogonLog() {
        try {
            final LogonLog logonLog = new LogonLog();
            logonLog.setUserId(randomAlphanumeric(65));

            verifyRequestLogonLogParamsConditions(logonLog);

            fail("The method should have thrown InvalidRequestException due to invalid userId");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(USERID_POST_EXCEPTION_MESSAGE);
        }
    }

    @Test
    void shouldNotVerifyEmailAddressForLogonLog() {
        try {
            final LogonLog logonLog = new LogonLog();
            logonLog.setEmailAddress(randomAlphanumeric(71));

            verifyRequestLogonLogParamsConditions(logonLog);

            fail("The method should have thrown InvalidRequestException due to invalid email");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE);
        }
    }

    @Test
    void shouldNotVerifyServiceForLogonLog() {
        try {
            final LogonLog logonLog = new LogonLog();
            logonLog.setService(randomAlphanumeric(71));

            verifyRequestLogonLogParamsConditions(logonLog);

            fail("The method should have thrown InvalidRequestException due to invalid service");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(SERVICE_POST_EXCEPTION_MESSAGE);
        }
    }

    @Test
    void shouldNotVerifyIpaddressForLogonLog() {
        try {
            final LogonLog logonLog = new LogonLog();
            logonLog.setIpAddress(randomAlphanumeric(71));

            verifyRequestLogonLogParamsConditions(logonLog);

            fail("The method should have thrown InvalidRequestException due to invalid ip address");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(IPADDRESS_POST_EXCEPTION_MESSAGE);
        }
    }

    @Test
    void shouldNotVerifyTimestampForLogon() {
        try {
            final LogonLog logonLog = new LogonLog();
            logonLog.setTimestamp("2021-106-23T22:20:05");

            verifyRequestLogonLogParamsConditions(logonLog);

            fail("The method should have thrown InvalidRequestException due to invalid timestamp");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(TIMESTAMP_POST_EXCEPTION_MESSAGE);
        }
    }

}