package uk.gov.hmcts.reform.laubackend.idam.exceptions;

public class InvalidServiceAuthorizationException extends RuntimeException {
    private static final long serialVersionUID = -4L;

    public InvalidServiceAuthorizationException(final String message) {
        super(message);
    }
}
