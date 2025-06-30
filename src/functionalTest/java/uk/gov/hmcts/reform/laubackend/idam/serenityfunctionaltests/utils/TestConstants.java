package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.utils;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestConstants {

    public static final String FRONTEND_SERVICE_NAME = "lau_frontend";
    public static final String DISPOSER_USER_SERVICE_NAME = "disposer-idam-user";
    public static final String INVALID_SERVICE_NAME = "invalid-service";

    /*Logon endPoint*/
    public static final String LOGON_ENDPOINT = "/audit/logon";
    public static final String LOGON_DELETE_ENDPOINT = "/audit/logon/deleteAuditLogonRecord";
    public static final String SUCCESS = "Success";

    /* deleted accounts endpoint */
    public static final String DELETED_ACCOUNTS_ENDPOINT = "/audit/deletedAccounts";
    public static final String ALL_DELETED_ACCOUNTS_ENDPOINT = "/audit/getAllDeletedAccounts";
    public static final String DELETED_ACCOUNTS_DELETE_ENDPOINT = "/audit/idamUser/deleteIdamUserRecord";

    // Authorization constants
    public static final String SCOPE = "openid profile roles";

    private TestConstants() {

    }
}
