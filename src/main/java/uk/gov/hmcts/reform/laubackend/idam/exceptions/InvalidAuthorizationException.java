package uk.gov.hmcts.reform.laubackend.idam.exceptions;

public class InvalidAuthorizationException extends RuntimeException {
    private static final long serialVersionUID = -4L;

    public InvalidAuthorizationException(String message) {
        super(message);
    }
}
