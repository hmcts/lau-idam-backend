package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.config.EnvConfig.IDAM_CLIENT_SECRET;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants.CLIENT_ID;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants.SCOPE;

public class AuthorizationHeaderHelper {

    final PropertyReader propertyReader = PropertyReader.getInstance();
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AuthorizationHeaderHelper.class);

    public String getAuthorizationToken() throws JSONException {
        Response response = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                .formParam("grant_type", propertyReader.getPropertyValue("idam.grant.type"))
                .formParam("username", propertyReader.getPropertyValue("idam.username"))
                .formParam("password", propertyReader.getPropertyValue("idam.password"))
                .formParam("redirect_uri", propertyReader.getPropertyValueFromEnv("idam.redirect.url"))
                .formParam("scope", SCOPE)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", IDAM_CLIENT_SECRET)
                .when()
                .post(propertyReader.getPropertyValueFromEnv("idam.token.url"));

        return "Bearer " + new JSONObject(response.getBody().asString())
                .getString("access_token");
    }


    public String getServiceToken(final String serviceName) {

        LOGGER.info("s2sUrl lease url: {}", propertyReader.getPropertyValueFromEnv("s2s.url") + "/lease");
        final Map<String, Object> params = Map.of("microservice", serviceName);

        final Response response = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(propertyReader.getPropertyValueFromEnv("s2s.url"))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/lease")
                .andReturn();
        assertThat(response.getStatusCode()).isEqualTo(200);

        return "Bearer " + response
                .getBody()
                .asString();
    }
}
