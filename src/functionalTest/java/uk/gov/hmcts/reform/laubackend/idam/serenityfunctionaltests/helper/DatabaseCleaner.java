package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccount;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.DeletedAccountsResponse;
import uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model.LogonLogPostResponseVO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants.LOGON_DELETE_ENDPOINT;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants.DELETED_ACCOUNTS_DELETE_ENDPOINT;
import static uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils.TestConstants.FRONTEND_SERVICE_NAME;

@Slf4j
public final class DatabaseCleaner {

    private DatabaseCleaner() {
    }

    public static void deleteLogonRecord(final Response response) {
        final Gson jsonReader = new Gson();
        final LogonLogPostResponseVO logonLogPostResponse = jsonReader
                .fromJson(response.getBody().asString(), LogonLogPostResponseVO.class);

        try {
            makeRequest(LOGON_DELETE_ENDPOINT, "logonId", logonLogPostResponse.getLogonLog().getId(), OK.value());
        } catch (JSONException je) {
            log.error(je.getMessage(), je);
        }
    }

    public static void deleteDeletedAccountRecord(Response response) {
        final Gson jsonReader = new Gson();
        DeletedAccountsResponse deletedAccountsResponse = jsonReader.fromJson(
            response.getBody().asString(),
            DeletedAccountsResponse.class);

        try {
            for (DeletedAccount deletedAccount: deletedAccountsResponse.getDeletionLogs()) {
                String userId = deletedAccount.getUserId();
                makeRequest(DELETED_ACCOUNTS_DELETE_ENDPOINT, "userId", userId, NO_CONTENT.value());
            }
        } catch (JSONException je) {
            log.error(je.getMessage(), je);
        }
    }

    private static void makeRequest(
        String endpoint,
        String paramName,
        String paramValue,
        int expectedStatusCode) throws JSONException {

        final PropertyReader propertyReader = PropertyReader.getInstance();
        final AuthorizationHeaderHelper authorizationHeaderHelper = new AuthorizationHeaderHelper();
        final Response deleteResponse = RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(propertyReader.getPropertyValueFromEnv("api.url") + endpoint)
            .queryParam(paramName, paramValue)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header(SERVICE_AUTHORISATION_HEADER, authorizationHeaderHelper.getServiceToken(FRONTEND_SERVICE_NAME))
            .header(AUTHORISATION_HEADER, authorizationHeaderHelper.getAuthorizationToken())
            .when()
            .delete()
            .andReturn();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(expectedStatusCode);
    }
}
