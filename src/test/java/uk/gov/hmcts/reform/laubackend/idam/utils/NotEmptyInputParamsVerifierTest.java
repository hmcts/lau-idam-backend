package uk.gov.hmcts.reform.laubackend.idam.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyLogonLogRequestAreNotEmpty;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotEmptyInputParamsVerifierTest {

    @Test
    void shouldThrowExceptionWhenPostRequestParamsAreEmptyForLogonLog() {
        try {
            verifyLogonLogRequestAreNotEmpty(new LogonLog("1",
                    "2",
                    null,
                    "4",
                    "5"));

            fail("The method should have thrown InvalidRequestException when all required params are not populated");
        } catch (final InvalidRequestException invalidRequestException) {
            assertThat(invalidRequestException.getMessage())
                    .isEqualTo("You need to populate all required parameters - "
                            + "userId, email, service, ipAddress and timestamp ");
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
}