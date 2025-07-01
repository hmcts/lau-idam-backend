package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

import static org.springdoc.core.utils.Constants.POST_METHOD;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.PreserveStackTrace", "PMD.ExceptionAsFlowControl",
    "PMD.DoNotUseThreads"})
public class ServiceAuthorizationAuthenticator {

    private final AuthService authService;

    private final AuthorisedServices authorisedServices;

    private final AsyncAuthService asyncAuthService;

    public void authorizeServiceToken(final HttpServletRequest request) {
        final String serviceAuthHeader = request.getHeader(SERVICE_AUTHORISATION_HEADER);
        String method = request.getMethod();

        if (POST_METHOD.equalsIgnoreCase(method)) {
            handlePostRequest(serviceAuthHeader);
        } else {
            handleOtherRequest(serviceAuthHeader);
        }
    }

    private void handlePostRequest(String serviceAuthHeader) {
        try {
            String serviceName = asyncAuthService.authenticateService(serviceAuthHeader).get();
            validateServiceName(serviceName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InvalidServiceAuthorizationException("Thread interrupted" + e.getMessage());
        } catch (Exception e) {
            throw new InvalidServiceAuthorizationException("Service authentication failed: " + e.getMessage());
        }
    }

    private void handleOtherRequest(String serviceAuthHeader) {
        try {
            final String serviceName = String.valueOf(authService.authenticateService(serviceAuthHeader));
            validateServiceName(serviceName);
        } catch (final Exception exception) {
            throw new InvalidServiceAuthorizationException(exception.getMessage());
        }
    }

    private void validateServiceName(String serviceName) {
        if (!authorisedServices.hasService(serviceName)) {
            log.info("Service {} has NOT been authorised!", serviceName);
            throw new InvalidServiceAuthorizationException("Unable to authenticate service name.");
        }
    }
}
