package uk.gov.hmcts.reform.laubackend.idam.insights;

public enum AppInsightsEvent {
    GET_LOGON_REQUEST_INFO("Audit GET IdAM Logon Request Info: "),
    GET_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION("Audit GET IdAM Logon Requst InvalidRequestException: "),
    POST_LOGON_REQUEST_EXCEPTION("Audit POST IdAM Logon Request Exception: "),
    POST_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION("Audit POST IdAM Logon Requst InvalidRequestException: ");

    private String displayName;

    AppInsightsEvent(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
