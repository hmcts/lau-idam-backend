package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@ExtendWith(MockitoExtension.class)
class RestApiPreInvokeInterceptorTest {

    @Mock
    private ObjectProvider<ServiceAuthorizationAuthenticator> serviceAuthorizationAuthenticatorProvider;

    @Mock
    private ServiceAuthorizationAuthenticator serviceAuthorizationAuthenticator;

    @Mock
    private ObjectProvider<AuthorizationAuthenticator> authorizationAuthenticatorProvider;

    @Mock
    private AuthorizationAuthenticator authorizationAuthenticator;

    private RestApiPreInvokeInterceptor restApiPreInvokeInterceptor;

    @BeforeEach
    void setUp() {
        restApiPreInvokeInterceptor = new RestApiPreInvokeInterceptor(
            serviceAuthorizationAuthenticatorProvider,
            authorizationAuthenticatorProvider
        );
    }

    @Test
    void shouldReturnTrueWhenServiceAndAuthTokenIsValid() throws IOException {
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        final Object object = mock(Object.class);

        when(serviceAuthorizationAuthenticatorProvider.getObject()).thenReturn(serviceAuthorizationAuthenticator);
        doNothing().when(serviceAuthorizationAuthenticator).authorizeServiceToken(httpServletRequest);

        when(httpServletRequest.getMethod()).thenReturn(POST.name());

        final boolean isValidRequest = restApiPreInvokeInterceptor
                .preHandle(httpServletRequest, httpServletResponse, object);

        assertThat(isValidRequest).isTrue();
    }

    @Test
    void shouldThrowInvalidServiceAuthorizationExceptionWhenServiceNameInvalid() throws IOException {
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        final Object object = mock(Object.class);

        when(serviceAuthorizationAuthenticatorProvider.getObject()).thenReturn(serviceAuthorizationAuthenticator);
        doThrow(new InvalidServiceAuthorizationException("Yabba Dabba Doo"))
                .when(serviceAuthorizationAuthenticator)
                .authorizeServiceToken(httpServletRequest);

        final boolean isValidRequest = restApiPreInvokeInterceptor
                .preHandle(httpServletRequest, httpServletResponse, object);

        assertThat(httpServletResponse.getErrorMessage())
                .isEqualTo("Yabba Dabba Doo");
        assertThat(httpServletResponse.getStatus())
                .isEqualTo(SC_FORBIDDEN);
        assertThat(isValidRequest).isFalse();
    }

    @Test
    void shouldThrowInvalidAuthorizationExceptionWhenMissingAuthHeader() throws IOException {
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        final Object object = mock(Object.class);

        when(serviceAuthorizationAuthenticatorProvider.getObject()).thenReturn(serviceAuthorizationAuthenticator);
        doNothing().when(serviceAuthorizationAuthenticator).authorizeServiceToken(httpServletRequest);
        when(httpServletRequest.getMethod()).thenReturn(GET.name());
        when(authorizationAuthenticatorProvider.getObject()).thenReturn(authorizationAuthenticator);
        doThrow(new InvalidAuthorizationException("Scooby Doo"))
                .when(authorizationAuthenticator)
                .authorizeAuthorizationToken(httpServletRequest);

        final boolean isValidRequest = restApiPreInvokeInterceptor
                .preHandle(httpServletRequest, httpServletResponse, object);

        assertThat(httpServletResponse.getErrorMessage())
                .isEqualTo("Scooby Doo");
        assertThat(httpServletResponse.getStatus())
                .isEqualTo(SC_UNAUTHORIZED);
        assertThat(isValidRequest).isFalse();
    }

}
