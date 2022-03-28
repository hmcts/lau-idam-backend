package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils;

public final class TestConstants {

    public static final String S2S_NAME = "lau_frontend";
    public static final String S2S_URL = "http://rpe-service-auth-provider-aat.service.core-compute-aat.internal/testing-support";

    /*Logon endPoint*/
    public static final String LOGON_ENDPOINT = "/audit/logon";
    public static final String LOGON_DELETE_ENDPOINT = "/audit/logon/deleteAuditLogonRecord";
    public static final String SUCCESS = "Success";

    // Authorization constants
    public static final String GRANT_TYPE = "password";
    public static final String USERNAME = "lautest@test.com";
    public static final String PASSWORD = "Password12";
    public static final String REDIRECT_URI = "https://lau-case.aat.platform.hmcts.net/oauth2/callback";
    public static final String SCOPE = "openid profile roles";
    public static final String CLIENT_ID = "lau";
    public static final String TOKEN_URL = "https://idam-api.aat.platform.hmcts.net/o/token";

    private TestConstants() {

    }
}
