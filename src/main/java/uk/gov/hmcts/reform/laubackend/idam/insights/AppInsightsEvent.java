package uk.gov.hmcts.reform.laubackend.idam.insights;

public enum AppInsightsEvent {
    GET_LOGON_REQUEST_INFO("Audit GET IdAM Logon Request Info: "),
    GET_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION("Audit GET IdAM Logon Request InvalidRequestException: "),
    GET_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION(
        "Audit GET IdAM Deleted Accounts InvalidRequestException"),
    GET_ALL_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION(
        "Audit GET All IdAM Deleted Accounts InvalidRequestException"),

    POST_LOGON_REQUEST_EXCEPTION("Audit POST IdAM Logon Request Exception: "),
    POST_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION("Audit POST IdAM Logon Request InvalidRequestException: "),

    POST_DELETION_REQUEST_EXCEPTION("Audit POST IdAM Deletion Request Exception: "),
    POST_DELETION_INVALID_REQUEST_EXCEPTION("Audit POST IdAM Deletion Request InvalidRequestException: ");

    private final String displayName;

    AppInsightsEvent(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
