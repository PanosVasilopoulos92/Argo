package org.viators.argo.common.exceptions;

public class StorageException extends BaseException {
    public StorageException(String message) {
        super(message, ErrorCodeEnum.STORAGE_ERROR);
    }

    public StorageException(String message, Throwable cause) {
        super(message, ErrorCodeEnum.STORAGE_ERROR, cause);
    }
}
