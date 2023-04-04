package uk.gov.hmcts.reform.laubackend.idam.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyLogonLogRequestAreNotEmpty;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyRequestLogonParamsAreNotEmpty;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotEmptyInputParamsVerifierTest {

    @Test
    void shouldThrowExceptionWhenPostRequestParamsAreEmptyForLogonLog() {
        try {
            verifyLogonLogRequestAreNotEmpty(new LogonLog("1",
                    null,
                    "3",
                    "4",
                    "5",
                    "6"));

            fail("The method should have thrown InvalidRequestException when all required params are not populated");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo("You need to populate all required parameters - "
                            + "userId, email and timestamp ");
        }
    }

    @Test
    void shouldNotThrowExceptionWhenPostRequestParamsAreEmptyForLogonLog() {
        assertDoesNotThrow(() -> verifyLogonLogRequestAreNotEmpty(new LogonLog("1",
                "2",
                "service",
                "4",
                "5",
                "6")));
    }

    @Test
    void shouldThrowExceptionWhenGetRequestParamsAreEmptyForLogonLog() {
        try {
            verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                    null,
                    null,
                    null,
                    null,
                    null));
            fail("The method should have thrown InvalidRequestException when all required params are not populated");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo("Both startTime and endTime must be present "
                                   + "and at least one of the parameters ((userId, emailAddress "
                                   + ") must not be empty");
        }
    }

    @Test
    void shouldVerifyRequestParamsAreNotEmptyForUserIdLogonLog() {
        assertDoesNotThrow(() -> verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(
            random(71, "123456"),
                null,
                "345",
                "735",
                null,
                null)));
    }

    @Test
    void shouldVerifyRequestParamsAreNotEmptyForEmailAddressLogonLog() {
        assertDoesNotThrow(() -> verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                "test@test.com",
                "345",
                "735",
                null,
                null)));
    }

    @Test
    void shouldThrowExceptionWhenStartTimeAndEndTimeProvided() {
        try {
            verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                null,
                "345",
                "735",
                null,
                null));
            fail("The method should have thrown InvalidRequestException when startTime and endTime input params are "
                     + "provided");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage()).isEqualTo(
                "Both startTime and endTime must be present "
                    + "and at least one of the parameters ((userId, emailAddress "
                    + ") must not be empty");
        }
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsMissing() {
        try {
            verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                "test@test.com",
                null,
                "735",
                null,
                null));
            fail("The method should have thrown InvalidRequestException when startTime  input param is missing");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                .isEqualTo("Both startTime and endTime must be present "
                    + "and at least one of the parameters ((userId, emailAddress "
                    + ") must not be empty");
        }
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsMissing() {
        try {
            verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                "test@test.com",
                "345",
                null,
                null,
                null));
            fail("The method should have thrown InvalidRequestException when endTime input param is missing");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                .isEqualTo("Both startTime and endTime must be present "
                               + "and at least one of the parameters ((userId, emailAddress "
                               + ") must not be empty");
        }
    }

    @Test
    void shouldThrowExceptionWhenAllParametersAreMissing() {
        try {
            verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                null,
                null,
                null,
                null,
                null));
            fail("The method should have thrown InvalidRequestException when all input param is missing");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                .isEqualTo("Both startTime and endTime must be present "
                               + "and at least one of the parameters ((userId, emailAddress "
                               + ") must not be empty");
        }
    }

}
