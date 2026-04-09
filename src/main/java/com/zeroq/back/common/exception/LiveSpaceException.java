package com.zeroq.back.common.exception;

import web.common.core.response.base.exception.GeneralException;
import web.common.core.response.base.vo.Code;

public class LiveSpaceException extends GeneralException {

    public LiveSpaceException(Code errorCode, String message) {
        super(errorCode, message);
    }

    public Integer getCode() {
        return getErrorCode().getCode();
    }

    public int getStatus() {
        return getErrorCode().getHttpStatus().value();
    }

    // 자주 사용되는 예외들
    public static class ResourceNotFoundException extends LiveSpaceException {
        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(
                    Code.NOT_FOUND,
                    String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue)
            );
        }
    }

    public static class ValidationException extends LiveSpaceException {
        public ValidationException(String message) {
            super(Code.VALIDATION_ERROR, message);
        }
    }

    public static class UnauthorizedException extends LiveSpaceException {
        public UnauthorizedException(String message) {
            super(Code.UNAUTHORIZED, message);
        }
    }

    public static class ForbiddenException extends LiveSpaceException {
        public ForbiddenException(String message) {
            super(Code.FORBIDDEN, message);
        }
    }

    public static class ConflictException extends LiveSpaceException {
        public ConflictException(String message) {
            super(Code.CONFLICT, message);
        }
    }
}
