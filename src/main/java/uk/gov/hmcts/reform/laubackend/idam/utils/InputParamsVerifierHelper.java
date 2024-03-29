package uk.gov.hmcts.reform.laubackend.idam.utils;

import uk.gov.hmcts.reform.laubackend.idam.constants.LoginState;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static uk.gov.hmcts.reform.laubackend.idam.constants.ExceptionMessageConstants.appendExceptionParameter;

@SuppressWarnings({"PMD.TooManyMethods"})
public final class InputParamsVerifierHelper {

    private static final long MIN_POSITIVE_NUMBER = 1L;

    private InputParamsVerifierHelper() {
    }

    public static void verifyTimestamp(final String timestamp,
                                       final String exceptionMessage,
                                       final String regex) throws InvalidRequestException {
        if (!isEmpty(timestamp) && !compile(regex).matcher(timestamp).matches()) {
            throw new InvalidRequestException(appendExceptionParameter(exceptionMessage, timestamp), BAD_REQUEST);
        }
    }


    public static void verifyUserId(final String userId,
                                    final String exceptionMessage) throws InvalidRequestException {
        if (!isEmpty(userId) && userId.length() > 64) {
            throw new InvalidRequestException(appendExceptionParameter(exceptionMessage, userId), BAD_REQUEST);
        }
    }

    public static void verifyEmailAddress(final String emailAddress,
                                          final String exceptionMessage) throws InvalidRequestException {
        if (!isEmpty(emailAddress) && emailAddress.length() > 70) {
            throw new InvalidRequestException(exceptionMessage, BAD_REQUEST);
        }
    }

    public static void verifyService(final String service,
                                     final String exceptionMessage) throws InvalidRequestException {
        if (!isEmpty(service) && service.length() > 70) {
            throw new InvalidRequestException(appendExceptionParameter(exceptionMessage, service), BAD_REQUEST);
        }
    }

    public static void verifyIpAddress(final String ipAddress,
                                       final String exceptionMessage) throws InvalidRequestException {
        if (!isEmpty(ipAddress) && ipAddress.length() > 70) {
            throw new InvalidRequestException(exceptionMessage, BAD_REQUEST);
        }
    }

    public static void verifyIdNotEmpty(final String id)
            throws InvalidRequestException {
        if (isEmpty(id)) {
            throw new InvalidRequestException("Id must be present", BAD_REQUEST);
        }
    }

    public static void verifyLoginState(final String loginState,
                                    final String exceptionMessage) throws InvalidRequestException {
        if (!isEmpty(loginState) && !isValidEnum(LoginState.class, loginState)) {
            throw new InvalidRequestException(exceptionMessage, BAD_REQUEST);
        }
    }

    public static void verifyLength(String value, int length, String message) throws InvalidRequestException {
        if (!isEmpty(value) && value.length() > length) {
            throw new InvalidRequestException(message, BAD_REQUEST);
        }
    }

    public static void verifyPositiveNumeric(String value, String message) throws InvalidRequestException {
        if (!isEmpty(value)) {
            if (!isNumeric(value)) {
                throw new InvalidRequestException(message, BAD_REQUEST);
            }

            long number = Long.parseLong(value);
            if (number < MIN_POSITIVE_NUMBER) {
                throw new InvalidRequestException(message, BAD_REQUEST);
            }
        }
    }

    public static void verifyPositiveNumericAndNotNull(String value, String message) throws InvalidRequestException {
        if (isEmpty(value) || !isNumeric(value) || Long.parseLong(value) < MIN_POSITIVE_NUMBER) {
            throw new InvalidRequestException(message, BAD_REQUEST);
        }
    }

    public static void verifySort(final String sort,
                                    final String exceptionMessage) throws InvalidRequestException {
        if (!isEmpty(sort) && !("asc".equalsIgnoreCase(sort) || "desc".equalsIgnoreCase(sort))) {
            throw new InvalidRequestException(appendExceptionParameter(exceptionMessage, sort), BAD_REQUEST);
        }
    }
}
