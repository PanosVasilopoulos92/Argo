package org.viators.argo.common.exceptions;

public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException(String message) {
        super(message, ErrorCodeEnum.INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException() {
        super("Invalid Credentials provided", ErrorCodeEnum.INVALID_CREDENTIALS);
    }
}
