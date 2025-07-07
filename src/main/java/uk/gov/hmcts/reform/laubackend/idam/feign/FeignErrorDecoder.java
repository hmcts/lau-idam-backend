package uk.gov.hmcts.reform.laubackend.idam.feign;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.laubackend.idam.authorization.HttpPostRecordHolder;

@Configuration
@Slf4j
public class FeignErrorDecoder implements feign.codec.ErrorDecoder {

    @Autowired
    private HttpPostRecordHolder httpPostRecordHolder;

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        FeignException exception = FeignException.errorStatus(methodKey, response);
        log.info("Feign response status: {}, message - {}", status, exception.getMessage());
        if (response.status() >= 400
            && "GET".equalsIgnoreCase(response.request().httpMethod().name())
            && response.request().url().endsWith("/details")
            && httpPostRecordHolder.isPost()) {
            return new RetryableException(
                status,
                exception.getMessage(),
                response.request().httpMethod(),
                (Long) null, // unix timestamp *at which time* the request can be retried
                response.request()
            );
        }
        return exception;
    }
}
