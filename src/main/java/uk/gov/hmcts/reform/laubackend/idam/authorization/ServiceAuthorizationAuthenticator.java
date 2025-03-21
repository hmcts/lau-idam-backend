package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.PreserveStackTrace", "PMD.ExceptionAsFlowControl"})
public class ServiceAuthorizationAuthenticator {

    private final AuthService authService;

    private final AuthorisedServices authorisedServices;

    public void authorizeServiceToken(final HttpServletRequest request) {
        try {
            final String serviceAuthHeader = request.getHeader(SERVICE_AUTHORISATION_HEADER);
            final String serviceName = authService.authenticateService(serviceAuthHeader);

            if (!authorisedServices.hasService(serviceName)) {
                log.info("Service {} has NOT been authorised!", serviceName);
                throw new InvalidServiceAuthorizationException("Unable to authenticate service name.");
            }
        } catch (final Exception exception) {
            throw new InvalidServiceAuthorizationException(exception.getMessage());
        }
    }
}
