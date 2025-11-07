package com.sassi.smokehabits.validation;

import com.sassi.smokehabits.exception.ValidationException;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]");

    private static final int EMAIL_MAX_LENGTH = 254;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 128;
    private static final int CONTEXT_MIN_LENGTH = 2;
    private static final int CONTEXT_MAX_LENGTH = 100;
    private static final int CRAVING_MIN = 1;
    private static final int CRAVING_MAX = 10;
    private static final int REFRESH_TOKEN_MIN_LENGTH = 10;
    private static final int REFRESH_TOKEN_MAX_LENGTH = 10000;

    public static String validateAndSanitizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }

        String trimmed = email.trim().toLowerCase();

        if (trimmed.length() > EMAIL_MAX_LENGTH) {
            throw new ValidationException("Email must not exceed " + EMAIL_MAX_LENGTH + " characters");
        }

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Email format is invalid");
        }

        return trimmed;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password is required");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new ValidationException("Password must be at least " + PASSWORD_MIN_LENGTH + " characters long");
        }

        if (password.length() > PASSWORD_MAX_LENGTH) {
            throw new ValidationException("Password must not exceed " + PASSWORD_MAX_LENGTH + " characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter (A-Z)");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter (a-z)");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password must contain at least one number (0-9)");
        }

        return password;
    }

    public static String validateAndSanitizeContextLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ValidationException("Context label is required");
        }

        String trimmed = label.trim();

        if (trimmed.length() < CONTEXT_MIN_LENGTH) {
            throw new ValidationException("Context label must be at least " + CONTEXT_MIN_LENGTH + " characters");
        }

        if (trimmed.length() > CONTEXT_MAX_LENGTH) {
            throw new ValidationException("Context label must not exceed " + CONTEXT_MAX_LENGTH + " characters");
        }

        String sanitized = HTML_TAG_PATTERN.matcher(trimmed).replaceAll("");
        sanitized = CONTROL_CHAR_PATTERN.matcher(sanitized).replaceAll("");

        return sanitized;
    }

    public static String validateAndSanitizeHexColor(String color) {
        if (color == null || color.isBlank()) {
            throw new ValidationException("Color is required");
        }

        String trimmed = color.trim().toUpperCase();

        if (!HEX_COLOR_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Color must be a valid hex color (e.g., #FF0000 or #F00)");
        }

        return trimmed;
    }

    public static int validateCravingLevel(Integer cravingLevel) {
        if (cravingLevel == null) {
            throw new ValidationException("Craving level is required");
        }

        if (cravingLevel < CRAVING_MIN || cravingLevel > CRAVING_MAX) {
            throw new ValidationException("Craving level must be between " + CRAVING_MIN + " and " + CRAVING_MAX);
        }

        return cravingLevel;
    }

    public static String validateUUID(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new ValidationException("ID is required");
        }

        String trimmed = uuid.trim().toLowerCase();

        if (!UUID_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Invalid UUID format");
        }

        return trimmed;
    }

    public static String validateRefreshToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ValidationException("Refresh token is required");
        }

        String trimmed = token.trim();

        if (trimmed.length() < REFRESH_TOKEN_MIN_LENGTH) {
            throw new ValidationException("Refresh token is invalid");
        }

        if (trimmed.length() > REFRESH_TOKEN_MAX_LENGTH) {
            throw new ValidationException("Refresh token is invalid");
        }

        return trimmed;
    }

    public static void validateConsent(Boolean consent) {
        if (consent == null || !consent) {
            throw new ValidationException("You must consent to the terms and conditions");
        }
    }

    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }

        String trimmed = input.trim();
        String sanitized = HTML_TAG_PATTERN.matcher(trimmed).replaceAll("");
        sanitized = CONTROL_CHAR_PATTERN.matcher(sanitized).replaceAll("");

        return sanitized;
    }

    public static String validateStringLength(String input, int minLength, int maxLength, String fieldName) {
        if (input == null || input.isBlank()) {
            throw new ValidationException(fieldName + " is required");
        }

        String trimmed = input.trim();

        if (trimmed.length() < minLength) {
            throw new ValidationException(fieldName + " must be at least " + minLength + " characters");
        }

        if (trimmed.length() > maxLength) {
            throw new ValidationException(fieldName + " must not exceed " + maxLength + " characters");
        }

        return trimmed;
    }
}
