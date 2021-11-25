package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.config.EnvConfig;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;




public class BaseSteps {

    private static final RequestSpecification REQSPEC;
    private static final Logger LOGGER =
        LoggerFactory.getLogger(BaseSteps.class);
    private final String s2sUrl = TestConstants.S2S_URL;


    static {
        final String proxyHost = System.getProperty("http.proxyHost");
        final Integer proxyPort = proxyHost == null ? null : Integer.parseInt(System.getProperty("http.proxyPort"));

        final RestAssuredConfig config = RestAssuredConfig.newConfig()
            .encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset(StandardCharsets.UTF_8));

        final RequestSpecBuilder specBuilder = new RequestSpecBuilder()
            .setConfig(config)
            .setBaseUri(EnvConfig.API_URL)
            .setRelaxedHTTPSValidation();

        LOGGER.info("Using base API URL: " + EnvConfig.API_URL);
        if (proxyHost != null) {
            specBuilder.setProxy(proxyHost, proxyPort);
        }

        REQSPEC = specBuilder.build();
    }

    public RequestSpecification rest() {
        return SerenityRest.given(REQSPEC);
    }

    public RequestSpecification given() {
        return SerenityRest.given(REQSPEC);
    }


    public String getServiceToken(String s2sMicroServiceName) {

        LOGGER.info("s2sUrl lease url: {}", s2sUrl + "/lease");
        final Map<String, Object> params = of(
            "microservice", s2sMicroServiceName
        );

        final Response response = RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(s2sUrl)
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

    public Response performGetOperation(String endpoint,
                                        Map<String, String> headers,
                                        Map<String, String> queryParams, String authServiceToken) {

        RequestSpecification requestSpecification = rest().urlEncodingEnabled(false)
            .given().header("ServiceAuthorization", authServiceToken)
            .header("Content-Type", "application/json");


        if (null != headers && !headers.isEmpty()) {
            for (String headerKey : headers.keySet()) {
                requestSpecification.header(createHeader(headerKey, headers.get(headerKey)));
            }
        }

        if (null != queryParams && !queryParams.isEmpty()) {
            for (String queryParamKey : queryParams.keySet()) {
                requestSpecification.queryParam(queryParamKey, queryParams.get(queryParamKey));
            }
        }

        return requestSpecification.get(endpoint)
            .then()
            .extract().response();
    }

    public Header createHeader(String headerKey, String headerValue) {
        return new Header(headerKey, headerValue);
    }


    public Response performPostOperation(String endpoint,
                                         Map<String, String> headers,
                                         Map<String, String> queryParams,
                                         Object body,
                                         String authServiceToken
    ) throws JsonProcessingException {

        RequestSpecification requestSpecification = rest()
            .given().header("ServiceAuthorization", authServiceToken)
            .header("Content-Type", "application/json");
        if (null != headers && !headers.isEmpty()) {
            for (String headerKey : headers.keySet()) {
                requestSpecification.header(createHeader(headerKey, headers.get(headerKey)));
            }
        }
        if (null != queryParams && !queryParams.isEmpty()) {
            for (String queryParamKey : queryParams.keySet()) {
                requestSpecification.param(queryParamKey, queryParams.get(queryParamKey));
            }
        }
        String bodyJsonStr = null == body ? "" : new ObjectMapper().writeValueAsString(body);
        return requestSpecification.urlEncodingEnabled(true).body(bodyJsonStr).post(endpoint)
            .then()
            .extract().response();
    }
}
