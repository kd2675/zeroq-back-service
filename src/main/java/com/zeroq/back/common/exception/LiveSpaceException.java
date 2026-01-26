package com.zeroq.back.common.exception;

import lombok.Getter;

@Getter
public class LiveSpaceException extends RuntimeException {
    private final String code;
    private final int status;

    public LiveSpaceException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public LiveSpaceException(String code, String message) {
        super(message);
        this.code = code;
        this.status = 400;
    }

    // 자주 사용되는 예외들
    public static class ResourceNotFoundException extends LiveSpaceException {
        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(
                    "RESOURCE_NOT_FOUND",
                    String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                    404
            );
        }
    }

    public static class ValidationException extends LiveSpaceException {
        public ValidationException(String message) {
            super("VALIDATION_ERROR", message, 400);
        }
    }

    public static class UnauthorizedException extends LiveSpaceException {
        public UnauthorizedException(String message) {
            super("UNAUTHORIZED", message, 401);
        }
    }

    public static class ForbiddenException extends LiveSpaceException {
        public ForbiddenException(String message) {
            super("FORBIDDEN", message, 403);
        }
    }

    public static class ConflictException extends LiveSpaceException {
        public ConflictException(String message) {
            super("CONFLICT", message, 409);
        }
    }
}
