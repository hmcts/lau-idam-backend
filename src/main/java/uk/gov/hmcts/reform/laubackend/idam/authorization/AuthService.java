package uk.gov.hmcts.reform.laubackend.idam.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

@Component
public class AuthService {

    private final AuthTokenValidator authTokenValidator;
    private final IdamClient idamClient;


    @Autowired
    public AuthService(final AuthTokenValidator authTokenValidator,
                       final IdamClient idamClient) {
        this.authTokenValidator = authTokenValidator;
        this.idamClient = idamClient;
    }

    public String authenticateService(final String authHeader) {
        if (authHeader != null) {
            return authTokenValidator.getServiceName(authHeader);
        }
        throw new InvalidServiceAuthorizationException("Missing ServiceAuthorization header");
    }

    public UserInfo authorize(final String authHeader) {
        if (authHeader != null) {
            return idamClient.getUserInfo(authHeader);
        }
        throw new InvalidAuthorizationException("Missing Authorization header");
    }
}
