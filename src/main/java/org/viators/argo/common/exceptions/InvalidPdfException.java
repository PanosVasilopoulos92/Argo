package org.viators.argo.common.exceptions;

public class InvalidPdfException extends BaseException {
    public InvalidPdfException(String message) {
        super(message, ErrorCodeEnum.INVALID_PDF);
    }
}
