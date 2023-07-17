package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogonLogPostResponseVO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants.LOGON_DELETE_ENDPOINT;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants.FRONTEND_SERVICE_NAME;

public final class DatabaseCleaner {

    private DatabaseCleaner() {
    }

    public static void deleteLogonRecord(final Response response) throws JSONException {
        final Gson jsonReader = new Gson();
        final AuthorizationHeaderHelper authorizationHeaderHelper = new AuthorizationHeaderHelper();
        final PropertyReader propertyReader = PropertyReader.getInstance();
        final LogonLogPostResponseVO logonLogPostResponse = jsonReader
                .fromJson(response.getBody().asString(), LogonLogPostResponseVO.class);

        final Response deleteResponse = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(propertyReader.getPropertyValue("api.url") + LOGON_DELETE_ENDPOINT)
                .queryParam("logonId", logonLogPostResponse.getLogonLog().getId())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTHORISATION_HEADER, authorizationHeaderHelper.getServiceToken(FRONTEND_SERVICE_NAME))
                .header(AUTHORISATION_HEADER, authorizationHeaderHelper.getAuthorizationToken())
                .when()
                .delete()
                .andReturn();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(OK.value());
    }
}
