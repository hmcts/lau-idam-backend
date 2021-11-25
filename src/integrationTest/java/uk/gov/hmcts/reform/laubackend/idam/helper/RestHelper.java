package uk.gov.hmcts.reform.laubackend.idam.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.AUTH_TOKEN;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.SERVICE_AUTHORISATION_HEADER;

public final class RestHelper {

    private RestHelper() {
    }

    public static Response postObject(final Object object,
                                      final String path) {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .body(object)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + AUTH_TOKEN)
                .when()
                .post()
                .andReturn();
    }
}
