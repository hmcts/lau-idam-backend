package uk.gov.hmcts.reform.laubackend.idam.feign;

import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.laubackend.idam.authorization.HttpPostRecordHolder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD.CloseResource"})
@ExtendWith(MockitoExtension.class)
class FeignErrorDecoderTest {

    private static final String GET_METHOD = "GET";
    private static final String DETAILS_URL = "http://localhost/service/details";
    private static final String METHOD_KEY = "methodKey";

    @Mock
    private HttpPostRecordHolder httpPostRecordHolder;

    @InjectMocks
    private FeignErrorDecoder feignErrorDecoder;


    private Response buildResponse(int status, String method, String url) {
        Request request = Request.create(
            HttpMethod.valueOf(method),
            url,
            Collections.emptyMap(),
            null,
            StandardCharsets.UTF_8,
            null
        );
        return Response.builder()
            .status(status)
            .request(request)
            .build();
    }

    @Test
    void shouldReturnRetryableExceptionForGetRequestWithDetailsUrlAndStatus400() {
        Response response = buildResponse(401, GET_METHOD, DETAILS_URL);
        when(httpPostRecordHolder.isPost()).thenReturn(true);
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnRetryableExceptionForGetRequestWithDetailsUrlAndStatus500() {
        Response response = buildResponse(500, GET_METHOD, DETAILS_URL);
        when(httpPostRecordHolder.isPost()).thenReturn(true);
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnFeignExceptionForGetRequestWithoutDetailsUrl() {
        Response response = buildResponse(403, GET_METHOD, "http://localhost/service/validate");
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnFeignExceptionForPostRequestEvenWithDetailsUrl() {
        Response response = buildResponse(401, "POST", DETAILS_URL);
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnFeignExceptionForPutRequestWithDetailsUrl() {
        Response response = buildResponse(403, "PUT", DETAILS_URL);
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnFeignExceptionForGetRequestWithDetailsUrlAndStatus200() {
        Response response = buildResponse(200, GET_METHOD, DETAILS_URL);
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnFeignExceptionForGetRequestWithDetailsUrlAndStatus399() {
        Response response = buildResponse(399, GET_METHOD, DETAILS_URL);
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnRetryableExceptionForGetRequestWithDetailsInPath() {
        Response response = buildResponse(403, GET_METHOD, "http://localhost/api/v1/details/user");
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnRetryableExceptionForGetRequestWithDetailsAsQueryParam() {
        Response response = buildResponse(401, GET_METHOD, "http://localhost/api?endpoint=details&user=123");
        Exception ex = feignErrorDecoder.decode(METHOD_KEY, response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }
}

