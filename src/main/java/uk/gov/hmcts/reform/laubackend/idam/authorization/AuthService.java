package uk.gov.hmcts.reform.laubackend.idam.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.feign.ServiceAuthorizationFeignClient;

@Component
public class AuthService {

    private final IdamClient idamClient;

    private final ServiceAuthorizationFeignClient serviceAuthorizationFeignClient;


    @Autowired
    public AuthService(final ServiceAuthorizationFeignClient serviceAuthorizationFeignClient,
                       final IdamClient idamClient) {
        this.serviceAuthorizationFeignClient = serviceAuthorizationFeignClient;
        this.idamClient = idamClient;
    }

    public String authenticateService(final String authHeader) {
        if (authHeader != null) {
            return serviceAuthorizationFeignClient.getServiceName(authHeader);
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
