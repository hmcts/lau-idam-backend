package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestConstants {

    public static final String FRONTEND_SERVICE_NAME = "lau_frontend";
    public static final String USER_DISPOSER_SERVICE_NAME = "idam-user-disposer";

    /*Logon endPoint*/
    public static final String LOGON_ENDPOINT = "/audit/logon";
    public static final String LOGON_DELETE_ENDPOINT = "/audit/logon/deleteAuditLogonRecord";
    public static final String SUCCESS = "Success";

    /* deleted accounts endpoint */
    public static final String DELETED_ACCOUNTS_ENDPOINT = "/audit/deletedAccounts";
    public static final String DELETED_ACCOUNTS_DELETE_ENDPOINT = "/audit/idamUser/deleteIdamUserRecord";

    // Authorization constants
    public static final String SCOPE = "openid profile roles";

    // ClientSecret
    public static final String IDAM_CLIENT_SECRET = "IDAM_CLIENT_SECRET";

    private TestConstants() {

    }
}
