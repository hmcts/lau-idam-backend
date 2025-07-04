package uk.gov.hmcts.reform.laubackend.idam.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(
    name = "ServiceAuthorizationFeignClient",
    url = "${idam.s2s-auth.url}"
)
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServiceAuthorizationFeignClient {

    @GetMapping("/details")
    String getServiceName(@RequestHeader(AUTHORIZATION) String authHeader);

}
