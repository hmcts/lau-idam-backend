package uk.gov.hmcts.reform.laubackend.idam.constants;

public final class RequestConstants {

    public static final String USER_ID = "userId";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String START_TIME = "startTimestamp";
    public static final String END_TIME = "endTimestamp";
    public static final String SIZE = "size";
    public static final String PAGE = "page";

    public static final long PERF_TOLERANCE_THRESHOLD_MS = 1500;
    public static final String PERF_THRESHOLD_MESSAGE_BELOW =
        "Good: below threshold (" + PERF_TOLERANCE_THRESHOLD_MS + "ms)";
    public static final String PERF_THRESHOLD_MESSAGE_ABOVE =
        "Bad: above threshold (" + PERF_TOLERANCE_THRESHOLD_MS + "ms)";

    private RequestConstants() {
    }
}
