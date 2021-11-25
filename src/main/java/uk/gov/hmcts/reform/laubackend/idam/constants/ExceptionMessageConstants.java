package uk.gov.hmcts.reform.laubackend.idam.constants;

public final class ExceptionMessageConstants {
    //POST exception messages
    public static final String USERID_POST_EXCEPTION_MESSAGE = "Unable to verify userId pattern";
    public static final String EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE = "Unable to verify emailAddress pattern";
    public static final String SERVICE_POST_EXCEPTION_MESSAGE = "Unable to verify service pattern";
    public static final String IPADDRESS_POST_EXCEPTION_MESSAGE = "Unable to verify ipAddress pattern";
    public static final String TIMESTAMP_POST_EXCEPTION_MESSAGE = "Unable to verify timestamp pattern";


    private ExceptionMessageConstants() {
    }
}
