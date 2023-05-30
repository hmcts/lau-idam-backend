package uk.gov.hmcts.reform.laubackend.idam.constants;

import static java.lang.String.valueOf;

public final class ExceptionMessageConstants {
    //GET exception messages
    public static final String TIMESTAMP_GET_EXCEPTION_MESSAGE = "Unable to verify timestamp path parameter pattern: ";
    public static final String EMAIL_ADDRESS_GET_EXCEPTION_MESSAGE = "Unable to verify emailAddress parameter pattern";
    public static final String USERID_GET_EXCEPTION_MESSAGE = "Unable to verify userId path parameter pattern: ";
    public static final String MISSING_GET_USER_DELETION_PARAMETERS_MESSAGE =
        "Start and End timestamps are required and at least one of userId, emailAddress, firstName or lastName";
    public static final String FIRSTNAME_GET_EXCEPTION_MESSAGE = "Unable to verify first name parameter pattern";
    public static final String LASTNAME_GET_EXCEPTION_MESSAGE = "Unable to verify last name parameter pattern";
    public static final String PAGE_SIZE_EXCEPTION_MESSAGE = "Page size has to be a positive whole number";
    public static final String PAGE_EXCEPTION_MESSAGE = "Page number has to be a positive whole number";

    //POST exception messages
    public static final String USERID_POST_EXCEPTION_MESSAGE = "Unable to verify userId pattern: ";
    public static final String EMAIL_ADDRESS_POST_EXCEPTION_MESSAGE = "Unable to verify emailAddress pattern";
    public static final String SERVICE_POST_EXCEPTION_MESSAGE = "Unable to verify service pattern: ";
    public static final String IPADDRESS_POST_EXCEPTION_MESSAGE = "Unable to verify ipAddress pattern";
    public static final String TIMESTAMP_POST_EXCEPTION_MESSAGE = "Unable to verify timestamp pattern: ";
    public static final String LOGIN_STATE_EXCEPTION_MESSAGE = "Unable to verify loginState pattern";

    public static final String FIRSTNAME_POST_EXCEPTION_MESSAGE = "Unable to verify first name pattern";
    public static final String LASTNAME_POST_EXCEPTION_MESSAGE = "Unable to verify last name pattern";

    public static String appendExceptionParameter(final String exceptionMessage,
                                                  final String exceptionParameter) {
        return exceptionMessage.concat(valueOf(exceptionParameter));
    }


    private ExceptionMessageConstants() {
    }
}
