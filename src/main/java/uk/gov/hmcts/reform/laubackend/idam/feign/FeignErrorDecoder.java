package uk.gov.hmcts.reform.laubackend.idam.feign;

import feign.FeignException;
import feign.Response;
import feign.Request.HttpMethod;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@Slf4j
public class FeignErrorDecoder implements feign.codec.ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus respStatus = HttpStatus.valueOf(response.status());
        FeignException exception = FeignException.errorStatus(methodKey, response);
        log.info("Idam backend: Feign response status: {}, message - {}", response.status(), exception.getMessage());
        if (respStatus.is5xxServerError()
            && HttpMethod.GET == response.request().httpMethod()
            && response.request().url().endsWith("/details")) {
            return new RetryableException(
                response.status(),
                exception.getMessage(),
                response.request().httpMethod(),
                (Long) null, // unix timestamp *at which time* the request can be retried
                response.request()
            );
        }
        return exception;
    }
}
