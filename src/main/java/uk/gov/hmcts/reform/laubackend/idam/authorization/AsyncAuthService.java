package uk.gov.hmcts.reform.laubackend.idam.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.feign.ServiceAuthorizationFeignClient;

import java.util.concurrent.CompletableFuture;

@Component
public class AsyncAuthService {

    private final ServiceAuthorizationFeignClient serviceAuthorizationFeignClient;

    @Autowired
    public AsyncAuthService(final ServiceAuthorizationFeignClient serviceAuthorizationFeignClient) {
        this.serviceAuthorizationFeignClient = serviceAuthorizationFeignClient;
    }

    @Async("TaskExecutor")
    public CompletableFuture<String> authenticateService(final String authHeader) {
        if (authHeader != null) {
            return CompletableFuture.completedFuture(serviceAuthorizationFeignClient.getServiceName(authHeader));
        }
        throw new InvalidServiceAuthorizationException("Missing ServiceAuthorization header in AsyncAuthService");
    }
}
