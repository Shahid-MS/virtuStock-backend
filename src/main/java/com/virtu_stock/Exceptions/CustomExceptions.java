package com.virtu_stock.Exceptions;

public class CustomExceptions {
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidPaginationParameterException extends RuntimeException {
        public InvalidPaginationParameterException(String message) {
            super(message);
        }
    }

    public static class InvalidSortFieldException extends RuntimeException {
        public InvalidSortFieldException(String message) {
            super(message);
        }
    }

}
