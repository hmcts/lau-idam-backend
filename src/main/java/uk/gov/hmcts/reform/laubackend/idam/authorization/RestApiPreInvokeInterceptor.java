package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
public class RestApiPreInvokeInterceptor implements HandlerInterceptor {

    @Autowired
    private ServiceAuthorizationAuthenticator serviceAuthorizationAuthenticator;

    @Autowired
    private AuthorizationAuthenticator authorizationAuthenticator;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws IOException {

        try {
            serviceAuthorizationAuthenticator.authorizeServiceToken(request);

            if (request.getMethod().equalsIgnoreCase(GET.name())
                || request.getMethod().equalsIgnoreCase(DELETE.name())) {
                authorizationAuthenticator.authorizeAuthorizationToken(request);
            }

        } catch (final InvalidServiceAuthorizationException exception) {
            log.error(
                "Token validation failed due to the following exception - {}",
                exception.getMessage(),
                exception
            );
            response.sendError(SC_FORBIDDEN, exception.getMessage());

            return false;

        } catch (final InvalidAuthorizationException exception) {
            log.error(
                "Authorization token failed due to error - {}",
                exception.getMessage(),
                exception
            );
            response.sendError(SC_UNAUTHORIZED, exception.getMessage());

            return false;
        } catch (final Exception exception) {
            log.error(
                "Service authorization token failed due to error - {}",
                exception.getMessage(),
                exception
            );
            response.sendError(SC_FORBIDDEN, exception.getMessage());

            return false;

        }
        return true;
    }
}

