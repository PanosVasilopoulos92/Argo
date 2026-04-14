package org.viators.argo.common.exceptions;

public class DuplicateResourceException extends BaseException {
    public DuplicateResourceException(String resourceType, String field, Object value) {
        super(
                String.format("%s with %s: '%s' already exists", resourceType, field, value),
                ErrorCodeEnum.DUPLICATE_RESOURCE
        );
    }

    public DuplicateResourceException(String message) {
        super(
                message,
                ErrorCodeEnum.DUPLICATE_RESOURCE
        );
    }
}
