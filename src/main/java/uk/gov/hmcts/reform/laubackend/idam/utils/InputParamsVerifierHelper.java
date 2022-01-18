package uk.gov.hmcts.reform.laubackend.idam.utils;

import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public final class InputParamsVerifierHelper {

    private InputParamsVerifierHelper() {
    }

    public static void verifyTimestamp(final String timestamp,
                                       final String exceptionMessage,
                                       final String regex) throws InvalidRequestException {
        if (!isEmpty(timestamp) && !compile(regex).matcher(timestamp).matches()) {
            throw new InvalidRequestException(exceptionMessage, BAD_REQUEST);
        }
    }


    public static void verifyUserId(final String userId,
                                    final String exceptionMessage) throws InvalidRequestException {
        if (!isEmpty(userId) && userId.length() > 64) {
            throw new InvalidRequestException(exceptionMessage, BAD_REQUEST);
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
            throw new InvalidRequestException(exceptionMessage, BAD_REQUEST);
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

}
