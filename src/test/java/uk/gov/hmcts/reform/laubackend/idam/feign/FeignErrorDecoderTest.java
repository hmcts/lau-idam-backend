package uk.gov.hmcts.reform.laubackend.idam.feign;

import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"PMD.CloseResource"})
class FeignErrorDecoderTest {

    private final FeignErrorDecoder decoder = new FeignErrorDecoder();

    private Response buildResponse(int status, String method) {
        Request request = Request.create(
            HttpMethod.valueOf(method),
            "http://localhost/test",
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
    void shouldReturnRetryableExceptionForPostAndStatusAbove400() throws Exception {
        Response response = buildResponse(401, "POST");
        Exception ex = decoder.decode("methodKey", response);
        assertThat(ex).isInstanceOf(RetryableException.class);

    }

    @Test
    void shouldReturnFeignExceptionForGetAndStatus403() throws Exception {
        Response response = buildResponse(403, "GET");
        Exception ex = decoder.decode("methodKey", response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }

    @Test
    void shouldReturnFeignExceptionForPostAndStatusBelow400() throws Exception {
        Response response = buildResponse(200, "POST");
        Exception ex = decoder.decode("methodKey", response);
        assertThat(ex).isInstanceOf(FeignException.class)
            .isNotInstanceOf(RetryableException.class);
    }
}

