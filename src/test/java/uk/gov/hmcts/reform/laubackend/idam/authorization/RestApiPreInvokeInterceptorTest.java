package uk.gov.hmcts.reform.laubackend.idam.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestApiPreInvokeInterceptorTest {

    @Mock
    private ServiceAuthorizationAuthenticator serviceAuthorizationAuthenticator;

    @Mock
    private AuthorizationAuthenticator authorizationAuthenticator;

    @InjectMocks
    private RestApiPreInvokeInterceptor restApiPreInvokeInterceptor;

    @Test
    void shouldReturnTrueWhenServiceAndAuthTokenIsValid() throws IOException {
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        final Object object = mock(Object.class);

        doNothing().when(serviceAuthorizationAuthenticator).authorizeServiceToken(httpServletRequest);

        when(httpServletRequest.getMethod()).thenReturn(POST.name());

        final boolean isValidRequest = restApiPreInvokeInterceptor
                .preHandle(httpServletRequest, httpServletResponse, object);

        assertThat(isValidRequest).isEqualTo(true);
    }

    @Test
    void shouldThrowInvalidServiceAuthorizationExceptionWhenServiceNameInvalid() throws IOException {
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        final Object object = mock(Object.class);

        doThrow(new InvalidServiceAuthorizationException("Yabba Dabba Doo"))
                .when(serviceAuthorizationAuthenticator)
                .authorizeServiceToken(httpServletRequest);

        final boolean isValidRequest = restApiPreInvokeInterceptor
                .preHandle(httpServletRequest, httpServletResponse, object);

        assertThat(((MockHttpServletResponse) httpServletResponse).getErrorMessage())
                .isEqualTo("Yabba Dabba Doo");
        assertThat(((MockHttpServletResponse) httpServletResponse).getStatus())
                .isEqualTo(SC_FORBIDDEN);
        assertThat(isValidRequest).isEqualTo(false);
    }

    @Test
    void shouldThrowInvalidAuthorizationExceptionWhenMissingAuthHeader() throws IOException {
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        final Object object = mock(Object.class);

        doNothing().when(serviceAuthorizationAuthenticator).authorizeServiceToken(httpServletRequest);
        when(httpServletRequest.getMethod()).thenReturn(GET.name());
        doThrow(new InvalidAuthorizationException("Scooby Doo"))
                .when(authorizationAuthenticator)
                .authorizeAuthorizationToken(httpServletRequest);


        final boolean isValidRequest = restApiPreInvokeInterceptor
                .preHandle(httpServletRequest, httpServletResponse, object);

        assertThat(((MockHttpServletResponse) httpServletResponse).getErrorMessage())
                .isEqualTo("Scooby Doo");
        assertThat(((MockHttpServletResponse) httpServletResponse).getStatus())
                .isEqualTo(SC_UNAUTHORIZED);
        assertThat(isValidRequest).isEqualTo(false);
    }

}