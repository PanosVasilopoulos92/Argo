package org.viators.argo.common.exceptions;

public class FileSizeExceededException extends BaseException {
    public FileSizeExceededException(String message) {
        super(message, ErrorCodeEnum.CONTENT_TOO_LARGE);
    }
}
