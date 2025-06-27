package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;


@ExtendWith(MockitoExtension.class)
class ServiceAuthorizationAuthenticatorTest {

    public static final String VALID_SERVICE_AUTH_HEADER = "validAuthHeader";

    @Mock
    private AuthService authService;

    @Mock
    private AuthorisedServices authorisedServices;

    @Mock
    private AsyncAuthService asyncAuthService;

    @Mock
    private HttpPostRecordHolder httpPostRecordHolder;

    @InjectMocks
    private ServiceAuthorizationAuthenticator serviceAuthorizationAuthenticator;

    @Test
    void shouldAuthorizeServiceSuccessfullyForPostRequest() throws Exception {
        String validServiceName = "ValidServiceName";

        when(asyncAuthService.authenticateService(VALID_SERVICE_AUTH_HEADER))
            .thenReturn(CompletableFuture.completedFuture(validServiceName));
        when(authorisedServices.hasService(validServiceName)).thenReturn(true);

        assertDoesNotThrow(() -> serviceAuthorizationAuthenticator.authorizeServiceToken(
            mockRequest(VALID_SERVICE_AUTH_HEADER, "POST")
        ));
    }

    @Test
    void shouldThrowExceptionForUnauthorizedServiceInPostRequest() throws Exception {
        String validServiceAuthHeader = "validAuthHeader";

        CompletableFuture<String> failedFuture = CompletableFuture.failedFuture(
            new InvalidServiceAuthorizationException("Forbidden")
        );
        when(asyncAuthService.authenticateService(validServiceAuthHeader)).thenReturn(failedFuture);

        Throwable thrown = catchThrowable(() ->
                                              serviceAuthorizationAuthenticator.authorizeServiceToken(
                                                  mockRequest(validServiceAuthHeader, "POST"))
        );

        assertThat(thrown)
            .isInstanceOf(InvalidServiceAuthorizationException.class)
            .hasMessage("Service authentication failed: uk.gov.hmcts.reform.laubackend.idam.exceptions."
                            + "InvalidServiceAuthorizationException: Forbidden");
    }

    @Test
    void shouldAuthorizeServiceSuccessfullyForOtherRequest() {
        String validServiceName = "ValidServiceName";

        when(authService.authenticateService(VALID_SERVICE_AUTH_HEADER))
            .thenReturn(validServiceName);
        when(authorisedServices.hasService(validServiceName)).thenReturn(true);

        assertDoesNotThrow(() -> serviceAuthorizationAuthenticator.authorizeServiceToken(
            mockRequest(VALID_SERVICE_AUTH_HEADER, "GET")));

    }

    @Test
    void shouldThrowExceptionForUnauthorizedServiceInOtherRequest() {
        String invalidServiceName = "InvalidServiceName";

        when(authService.authenticateService(VALID_SERVICE_AUTH_HEADER))
            .thenReturn(invalidServiceName);
        when(authorisedServices.hasService(invalidServiceName)).thenReturn(false);

        InvalidServiceAuthorizationException exception = assertThrows(
            InvalidServiceAuthorizationException.class,
            () -> serviceAuthorizationAuthenticator.authorizeServiceToken(mockRequest(VALID_SERVICE_AUTH_HEADER, "GET"))
        );

        assertEquals("Unable to authenticate service name.", exception.getMessage(),
                     "Not able to authenticate service name as expected");
    }

    @Test
    void shouldHandleExceptionGracefullyInOtherRequest() {

        when(authService.authenticateService(VALID_SERVICE_AUTH_HEADER))
            .thenThrow(new RuntimeException("Unexpected error"));

        InvalidServiceAuthorizationException exception = assertThrows(
            InvalidServiceAuthorizationException.class,
            () -> serviceAuthorizationAuthenticator.authorizeServiceToken(mockRequest(VALID_SERVICE_AUTH_HEADER, "GET"))
        );

        assertEquals("Unexpected error", exception.getMessage(),
                     "Exception message not matching with expected value");
    }

    private HttpServletRequest mockRequest(String serviceAuthHeader, String method) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(SERVICE_AUTHORISATION_HEADER)).thenReturn(serviceAuthHeader);
        when(request.getMethod()).thenReturn(method);
        return request;
    }
}
