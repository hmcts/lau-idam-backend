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

    // Authorization constants
    public static final String SCOPE = "openid profile roles";
    public static final String CLIENT_ID = "lau";

    private TestConstants() {

    }
}
