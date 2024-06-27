package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthorizationException;

import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.AUTHORISATION_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.PreserveStackTrace"})
public class AuthorizationAuthenticator {

    private final AuthService authService;

    private final AuthorisedServices authorisedServices;

    public void authorizeAuthorizationToken(final HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader(AUTHORISATION_HEADER);
            final UserInfo userInfo = authService.authorize(authHeader);

            if (!authorisedServices.hasRole(userInfo.getRoles())) {
                log.info("User {} has NOT been authorised!", userInfo.getName().concat(" ")
                        .concat(userInfo.getFamilyName()));
                throw new InvalidAuthorizationException("Unable to authorize user.");
            }
        } catch (final Exception exception) {
            throw new InvalidAuthorizationException(exception.getMessage());
        }
    }
}
