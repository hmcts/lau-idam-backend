package uk.gov.hmcts.reform.laubackend.idam.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
                .when()
                .post()
                .andReturn();
    }
}
