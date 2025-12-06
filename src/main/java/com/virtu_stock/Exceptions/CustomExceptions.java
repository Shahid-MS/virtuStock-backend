package com.virtu_stock.Exceptions;

public class CustomExceptions {
    // 400 - Bad Request
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    // 401 - Unauthorized
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    // 404 - Resource Not Found
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(resourceName + " not found with " + fieldName + ": " + fieldValue);
        }
    }

    // 409 - Conflict / Duplicate Resource
    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) {
            super(message);
        }
    }

    // Invalid pagination parameter
    public static class InvalidPaginationParameterException extends RuntimeException {
        public InvalidPaginationParameterException(String message) {
            super(message);
        }
    }

    // Invalid sorting
    public static class InvalidSortFieldException extends RuntimeException {
        public InvalidSortFieldException(String message) {
            super(message);
        }
    }

    // Generic invalid request
    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }

    public static class InvalidEmailAddressException extends RuntimeException {
        public InvalidEmailAddressException(String message) {
            super(message);
        }
    }

    public static class MailDeliveryException extends RuntimeException {
        public MailDeliveryException(String message) {
            super(message);
        }
    }

    public static class EmailFormattingException extends RuntimeException {
        public EmailFormattingException(String message) {
            super(message);
        }
    }

}
