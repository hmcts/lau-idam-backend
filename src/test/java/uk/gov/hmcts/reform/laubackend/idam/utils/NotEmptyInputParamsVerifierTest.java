package uk.gov.hmcts.reform.laubackend.idam.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyLogonLogRequestAreNotEmpty;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyRequestLogonParamsAreNotEmpty;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyUserDeletionGetRequestParamsPresence;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
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

    @Test
    void shouldNotThrowExceptionWhenUserDeleteAuditGetParamsArePopulated() {
        String start = "2023-05-23T09:23:54";
        String end = "2023-05-24T09:23:54";
        final DeletionLogGetRequestParams p1 = new DeletionLogGetRequestParams(
            "userId", "", "", "", start, end, null, null);
        assertDoesNotThrow(() -> verifyUserDeletionGetRequestParamsPresence(p1));

        final DeletionLogGetRequestParams p2 = new DeletionLogGetRequestParams(
            "", "email", "", "", start, end, null, null);
        assertDoesNotThrow(() -> verifyUserDeletionGetRequestParamsPresence(p2));

        final DeletionLogGetRequestParams p3 = new DeletionLogGetRequestParams(
            "", null, "John", "", start, end, null, null);
        assertDoesNotThrow(() -> verifyUserDeletionGetRequestParamsPresence(p3));

        final DeletionLogGetRequestParams p4 = new DeletionLogGetRequestParams(
            null, "", "", "Smith", start, end, null, null);
        assertDoesNotThrow(() -> verifyUserDeletionGetRequestParamsPresence(p4));

        final DeletionLogGetRequestParams p5 = new DeletionLogGetRequestParams(
            null, "", "John", "Smith", start, end, null, null);
        assertDoesNotThrow(() -> verifyUserDeletionGetRequestParamsPresence(p5));

        final DeletionLogGetRequestParams p6 = new DeletionLogGetRequestParams(
            "userId", "email", "John", "Smith", start, end, null, null);
        assertDoesNotThrow(() -> verifyUserDeletionGetRequestParamsPresence(p6));

        final DeletionLogGetRequestParams p7 = new DeletionLogGetRequestParams(
            "userId", "email", null, null, start, end, null, null);
        assertDoesNotThrow(() -> verifyUserDeletionGetRequestParamsPresence(p7));
    }

    @Test
    void shouldThrowExceptionWhenRequiredUserDeleteAuditGetParamsAreNotPresent() {
        String start = "2023-05-23T09:23:54";
        String end = "2023-05-24T09:23:54";
        final DeletionLogGetRequestParams p1 = new DeletionLogGetRequestParams(
            "", "", "", "", start, end, null, null);
        assertThrows(InvalidRequestException.class, () -> verifyUserDeletionGetRequestParamsPresence(p1));

        final DeletionLogGetRequestParams p2 = new DeletionLogGetRequestParams(
            null, null, null, null, start, end, null, null);
        assertThrows(InvalidRequestException.class, () -> verifyUserDeletionGetRequestParamsPresence(p2));

        final DeletionLogGetRequestParams p3 = new DeletionLogGetRequestParams(
            "", null, "John", "", null, end, null, null);
        assertThrows(InvalidRequestException.class, () -> verifyUserDeletionGetRequestParamsPresence(p3));

        final DeletionLogGetRequestParams p4 = new DeletionLogGetRequestParams(
            null, "", "", "Smith", start, "", null, null);
        assertThrows(InvalidRequestException.class, () -> verifyUserDeletionGetRequestParamsPresence(p4));

        final DeletionLogGetRequestParams p5 = new DeletionLogGetRequestParams(
            null, "", "John", "Smith", null, "", null, null);
        assertThrows(InvalidRequestException.class, () -> verifyUserDeletionGetRequestParamsPresence(p5));

        final DeletionLogGetRequestParams p6 = new DeletionLogGetRequestParams(
            "userId", "email", "John", "Smith", "", end, null, null);
        assertThrows(InvalidRequestException.class, () -> verifyUserDeletionGetRequestParamsPresence(p6));
    }
}
