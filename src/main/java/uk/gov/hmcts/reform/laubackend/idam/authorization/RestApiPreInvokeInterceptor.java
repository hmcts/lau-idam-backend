package uk.gov.hmcts.reform.laubackend.idam.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Slf4j
public class RestApiPreInvokeInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthorisedServices authorisedServices;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws IOException {

        try {
            final String serviceAuthHeader = request.getHeader("ServiceAuthorization");
            final String serviceName = authService.authenticate(serviceAuthHeader);

            if (!authorisedServices.hasService(serviceName)) {
                log.info("Service {} has NOT been authorised!", serviceName);
                throw new InvalidAuthenticationException("Unable to authenticate service request.");
            }
        } catch (final Exception exception) {
            log.error("Token authorisation failed due to error - {}",
                    exception.getMessage(),
                    exception);
            response.sendError(SC_FORBIDDEN, exception.getMessage());

            return false;
        }
        return true;
    }
}
