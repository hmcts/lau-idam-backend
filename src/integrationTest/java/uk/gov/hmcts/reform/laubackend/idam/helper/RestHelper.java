package uk.gov.hmcts.reform.laubackend.idam.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.BAD_S2S_TOKEN;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.GOOD_TOKEN;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.SERVICE_AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.helper.RestConstants.SERVICE_UNAVAILABLE_TOKEN;

@SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
public class RestHelper {

    public static Response getResponseWithoutHeader(final String path) {

        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .when()
                .get()
                .andReturn();
    }

    public static Response getResponseWithoutAuthorizationHeader(final String path) {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .when()
                .get()
                .andReturn();
    }

    public Response getResponse(final String path,
                                final Map<String, String> queryParams) {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .queryParams(queryParams)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .header(AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .when()
                .get()
                .andReturn();
    }

    public static Response postObject(final Object object,
                                      final String path) {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .body(object)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .when()
                .post()
                .andReturn();
    }

    public Response deleteResponse(final String path,
                                   final String parameterName,
                                   final String parameterValue) {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .queryParam(parameterName, parameterValue)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .header(AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .when()
                .delete()
                .andReturn();
    }

    public Response deleteResponseWithoutServiceAuthHeader(final String path) {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .when()
                .delete()
                .andReturn();
    }

    public Response deleteResponseWithoutAuthHeader(final String path) {
        return RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(path)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + GOOD_TOKEN)
                .when()
                .delete()
                .andReturn();
    }

    public Response postObjectWithBadServiceHeader(final Object object, final String path) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(path)
            .body(object)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + BAD_S2S_TOKEN)
            .when()
            .post()
            .andReturn();
    }

    public Response postObjectWithServiceUnavailableHeader(final Object object, final String path) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(path)
            .body(object)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header(SERVICE_AUTHORISATION_HEADER, "Bearer " + SERVICE_UNAVAILABLE_TOKEN)
            .when()
            .post()
            .andReturn();
    }
}
