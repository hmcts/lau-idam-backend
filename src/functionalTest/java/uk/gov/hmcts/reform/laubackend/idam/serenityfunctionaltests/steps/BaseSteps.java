package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.annotations.Step;
import org.json.JSONException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper.AuthorizationHeaderHelper;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper.PropertyReader;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants;

import java.nio.charset.StandardCharsets;
import java.util.Map;


public class BaseSteps {
    static final PropertyReader PROPERTY_READER = PropertyReader.getInstance();
    private static final RequestSpecification REQSPEC;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(BaseSteps.class);
    protected final AuthorizationHeaderHelper authorizationHeaderHelper = new AuthorizationHeaderHelper();


    static {
        final String proxyHost = System.getProperty("http.proxyHost");
        final Integer proxyPort = proxyHost == null ? null : Integer.parseInt(System.getProperty("http.proxyPort"));

        final RestAssuredConfig config = RestAssuredConfig.newConfig()
                .encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset(StandardCharsets.UTF_8));

        final RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setConfig(config)
                .setBaseUri("http://lau-idam-backend-aat.service.core-compute-aat.internal")
                .setRelaxedHTTPSValidation();

        LOGGER.info("Using base API URL: " + "http://lau-idam-backend-aat.service.core-compute-aat.internal");
        if (proxyHost != null) {
            specBuilder.setProxy(proxyHost, proxyPort);
        }

        REQSPEC = specBuilder.build();
    }

    public RequestSpecification rest() {
        return SerenityRest.given(REQSPEC);
    }

    public Response performGetOperation(String endpoint,
                                        Map<String, String> headers,
                                        Map<String, String> queryParams,
                                        String authServiceToken, String authorizationToken) {

        RequestSpecification requestSpecification = rest().urlEncodingEnabled(false)
                .given().header("ServiceAuthorization", authServiceToken)
                .header("Authorization", authorizationToken)
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

    @Step("Given a valid service token is generated")
    public String givenAValidServiceTokenIsGenerated(final String serviceName) {
        return authorizationHeaderHelper.getServiceToken(serviceName);
    }

    @Step("Then a success response is returned")
    public String thenASuccessResposeIsReturned(Response response) {
        Assert.assertTrue(
            "Response status code is not 200, but it is " + response.getStatusCode(),
            response.statusCode() == 200 || response.statusCode() == 201
        );
        return TestConstants.SUCCESS;
    }

    @Step("Then a forbidden response is returned")
    public String thenAForbiddenResposeIsReturned(Response response) {
        Assert.assertEquals(
            "Response status code is not 403, but it is " + response.getStatusCode(),
            403, response.statusCode()
        );
        return TestConstants.SUCCESS;
    }

    @Step("When the POST service is invoked")
    public Response whenThePostServiceIsInvoked(
        String endpoint,
        String serviceToken,
        Object body
    ) throws JsonProcessingException {
        return performPostOperation(endpoint, null, null, body, serviceToken);
    }

    @SuppressWarnings({"PMD.SimplifiableTestAssertion"})
    @Step("Then bad response is returned")
    public String thenBadResponseIsReturned(Response response, int expectedStatusCode) {
        Assert.assertTrue(
            "Response status code is not " + expectedStatusCode + ", but it is " + response.getStatusCode(),
            response.statusCode() == expectedStatusCode
        );
        return TestConstants.SUCCESS;
    }

    @Step("And valid Authorization token is generated")
    public String validAuthorizationTokenIsGenerated() throws JSONException {
        return authorizationHeaderHelper.getAuthorizationToken();
    }
}
