package uk.gov.hmcts.reform.laubackend.idam.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.EMAIL_ADDRESS_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.TIMESTAMP_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.USERID_GET_EXCEPTION_MESSAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.appendExceptionParameter;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyRequestLogonParamsConditions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogonInputParamsVerifierGetExceptionTest {

    @Test
    void shouldNotVerifyUserId() {
        final String userId = randomAlphanumeric(65);
        try {
            final LogonInputParamsHolder inputParamsHolder = new LogonInputParamsHolder(userId,
                    null,
                    null,
                    null,
                    null,
                    null);
            verifyRequestLogonParamsConditions(inputParamsHolder);
            fail("The method should have thrown InvalidRequestException due to invalid userId");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(appendExceptionParameter(USERID_GET_EXCEPTION_MESSAGE, userId));
            assertThat(invalidRequestException.getErrorCode())
                    .isEqualTo(BAD_REQUEST);
        }
    }

    @Test
    void shouldNotVerifyEmailAddress() {
        try {
            final LogonInputParamsHolder inputParamsHolder = new LogonInputParamsHolder(null,
                    randomAlphanumeric(71),
                    null,
                    null,
                    null,
                    null);
            verifyRequestLogonParamsConditions(inputParamsHolder);
            fail("The method should have thrown InvalidRequestException due to invalid emailAddress");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(EMAIL_ADDRESS_GET_EXCEPTION_MESSAGE);
            assertThat(invalidRequestException.getErrorCode())
                    .isEqualTo(BAD_REQUEST);
        }
    }

    @Test
    void shouldNotVerifyTimestamp() {
        final String timestamp = "2021-106-23T22:20:05";
        try {
            final LogonInputParamsHolder inputParamsHolder = new LogonInputParamsHolder(null,
                    null,
                    timestamp,
                    null,
                    null,
                    null);
            verifyRequestLogonParamsConditions(inputParamsHolder);
            fail("The method should have thrown InvalidRequestException due to invalid timestamp");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo(appendExceptionParameter(TIMESTAMP_GET_EXCEPTION_MESSAGE, timestamp));
            assertThat(invalidRequestException.getErrorCode())
                    .isEqualTo(BAD_REQUEST);
        }
    }
}
