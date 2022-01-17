package uk.gov.hmcts.reform.laubackend.idam.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyLogonLogRequestAreNotEmpty;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyRequestLogonParamsAreNotEmpty;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotEmptyInputParamsVerifierTest {

    @Test
    void shouldThrowExceptionWhenPostRequestParamsAreEmptyForLogonLog() {
        try {
            verifyLogonLogRequestAreNotEmpty(new LogonLog("1",
                    "2",
                    null,
                    "4",
                    "5"));u

            fail("The method should have thrown InvalidRequestException when all required params are not populated");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo("You need to populate all required parameters - "
                            + "userId, email, service and timestamp ");
        }
    }

    @Test
    void shouldNotThrowExceptionWhenPostRequestParamsAreEmptyForLogonLog() {
        assertDoesNotThrow(() -> verifyLogonLogRequestAreNotEmpty(new LogonLog("1",
                "2",
                "service",
                "4",
                "5")));
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
                .isEqualTo("At least one path parameter must be present");
        }
    }

    @Test
    void shouldVerifyRequestParamsAreNotEmptyForUserIdLogonLog() {
        assertDoesNotThrow(() -> verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(randomNumeric(71),
                                                                                                  null,
                                                                                                  null,
                                                                                                  null,
                                                                                                  null,
                                                                                                  null)));
    }

    @Test
    void shouldVerifyRequestParamsAreNotEmptyForEmailAddressLogonLog() {
        assertDoesNotThrow(() -> verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                                                                                                "test@test.com",
                                                                                                null,
                                                                                                null,
                                                                                                null,
                                                                                                null)));
    }

    @Test
    void shouldVerifyRequestParamsAreNotEmptyForStartTimeLogonLog() {
        assertDoesNotThrow(() -> verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                                                                                                null,
                                                                                                "345",
                                                                                                null,
                                                                                                null,
                                                                                                null)));
    }

    @Test
    void shouldVerifyRequestParamsAreNotEmptyForEndTimeLogonLog() {
        assertDoesNotThrow(() -> verifyRequestLogonParamsAreNotEmpty(new LogonInputParamsHolder(null,
                                                                                                null,
                                                                                                null,
                                                                                                "735",
                                                                                                null,
                                                                                                null)));
    }

}
