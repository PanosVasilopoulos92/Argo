package org.viators.argo.common.exceptions;

public class InvalidStateException extends BaseException {
    public InvalidStateException(String message) {
        super(message, ErrorCodeEnum.INVALID_STATE);
    }

    public InvalidStateException(String resource, String currentState, String attemptedAction) {
        super(
                String.format("Cannot %s %s in %s state", attemptedAction, resource, currentState),
                ErrorCodeEnum.INVALID_STATE
        );
    }
}
