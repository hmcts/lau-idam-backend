package uk.gov.hmcts.reform.laubackend.idam.smoke;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SmokeTestConfiguration.class})
class SmokeTests {

    @Value("${test.instance.uri}")
    private String url;

    private static final int HTTP_OK = HttpStatus.OK.value();

    RequestSpecification requestSpec;

    @BeforeEach
    void setUp() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addParam("http.connection.timeout", "60000");
        builder.addParam("http.socket.timeout", "60000");
        builder.addParam("http.connection-manager.timeout", "60000");
        builder.setRelaxedHTTPSValidation();
        requestSpec = builder.build();
    }

    @Test
    void shouldGetOkStatusFromHealthEndpointForLauBackend() {

        ValidatableResponse response = given().spec(requestSpec)
            .when()
            .get(url + "/health")
            .then()
            .statusCode(HTTP_OK);
        assertTrue("Health endpoint should be HTTP 200 (ok)", okResponse(response));
    }

    private boolean okResponse(ValidatableResponse response) {
        return response.extract().statusCode() == HTTP_OK ? Boolean.TRUE : Boolean.FALSE;
    }
}
