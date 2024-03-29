package org.anotherkyle.commonlib;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.anotherkyle.commonlib.log.LogLevel;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ApplicationStatus implements ApiStatus {
    // General
    SUCCESS("SUCCESS", HttpStatus.OK, "0000", LogLevel.INFO, "Operation successful."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "0001", LogLevel.CRITICAL, "Internal server error occurred."),
    PERMISSION_DENIED("PERMISSION_DENIED", HttpStatus.FORBIDDEN, "0002", LogLevel.ERROR, "Permission denied."),
    INPUT_VALIDATE_FAILED("INPUT_VALIDATE_FAILED", HttpStatus.BAD_REQUEST, "0003", LogLevel.WARN, "Failed to validate the body of the current request."),
    URI_DOES_NOT_EXIST("URI_DOES_NOT_EXIST", HttpStatus.NOT_FOUND, "0004", LogLevel.WARN, "Route to the given URI does not exist."),
    BASE64_DECODE_FAILED("BASE64_DECODE_FAILED", HttpStatus.BAD_REQUEST, "0005", LogLevel.WARN, "Failed to decode the base64 string."),
    CALL_API_FAILED("CALL_API_FAILED", HttpStatus.BAD_GATEWAY, "0006", LogLevel.WARN, "Process the remote API call failed."),
    RECORD_NOT_FOUND("RECORD_NOT_FOUND", HttpStatus.NOT_FOUND, "0007", LogLevel.WARN, "No record is found with the given criteria."),
    VIOLATE_UNIQUE_CONSTRAINT("VIOLATE_UNIQUE_CONSTRAINT", HttpStatus.CONFLICT, "0008", LogLevel.ERROR, "Violation of the unique constraint when saving to database."),
    MISSING_SERVER_KEY("MISSING_SERVER_KEY", HttpStatus.INTERNAL_SERVER_ERROR, "0009", LogLevel.ERROR, "Missing server key. Please set the secret key first."),

    // Transaction
    CHANNEL_DOES_NOT_EXIST("CHANNEL_DOES_NOT_EXIST", HttpStatus.NOT_FOUND, "0100", LogLevel.WARN, "Specified channel configuration not found."),
    SAVE_TRANSACTION_FAILED("SAVE_TRANSACTION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "0101", LogLevel.ERROR, "Failed to save the transaction."),
    SAVE_PARAMETER_FAILED("SAVE_PARAMETER_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "0102", LogLevel.ERROR, "Failed to save the intercepted API parameters"),

    // Cryptor
    INIT_CRYPTOR_FAILED("INIT_CRYPTOR_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "0200", LogLevel.ERROR, "Cryptor initialization failed."),
    GET_KEY_FAILED("GET_KEY_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "0201", LogLevel.ERROR, "Failed to get the public key."),
    DECRYPTION_FAILED("DECRYPTION_FAILED", HttpStatus.BAD_REQUEST, "0202", LogLevel.WARN, "Failed to decrypt the secret message."),
    ENCRYPTION_FAILED("ENCRYPTION_FAILED", HttpStatus.BAD_REQUEST, "0203", LogLevel.WARN, "Failed to encrypt the secret message."),
    GENERATE_JWT_FAILED("GENERATE_JWT_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "0204", LogLevel.ERROR, "Failed to generate JWT."),
    INVALID_LICENSE("INVALID_LICENSE", HttpStatus.UNAUTHORIZED, "0205", LogLevel.ERROR, "The given JWT is invalid or expired."),
    ILLEGAL_JWT_CLAIM("ILLEGAL_JWT_CLAIM", HttpStatus.BAD_REQUEST, "0206", LogLevel.WARN, "The content of the decoded JWT claim is illegal"),

    // User Api
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "0300", LogLevel.WARN, "User does not exist in the database."),
    RESULT_BELOW_THRESHOLD("RESULT_BELOW_THRESHOLD", HttpStatus.BAD_REQUEST, "0303", LogLevel.ERROR, "The result is below the given threshold."),

    // Image Manager
    SAVE_IMAGE_FILE_FAILED("SAVE_IMAGE_FILE_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "0400", LogLevel.ERROR, "Failed to save image file to the disk."),
    SHRINK_IMAGE_FAILED("SHRINK_IMAGE_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "0401", LogLevel.ERROR, "Unexpected error when trying to shrink an image."),
    IMAGE_CHECK_FAILED("IMAGE_CHECK_FAILED", HttpStatus.BAD_REQUEST, "0402", LogLevel.ERROR, "Failed to pass the image check.");

    private static final Map<String, ApplicationStatus> FORMAT_MAP_BY_CODE = Stream
            .of(ApplicationStatus.values())
            .collect(Collectors.toMap(s -> s.code, Function.identity()));
    private static String APPLICATION_NAME;
    private final String name;
    private final HttpStatus httpStatus;
    private final String code;
    private final LogLevel logLevel;
    private final String message;

    ApplicationStatus(String name, HttpStatus httpStatus, String code, LogLevel logLevel, String message) {
        this.name = name;
        this.httpStatus = httpStatus;
        this.code = code;
        this.logLevel = logLevel;
        this.message = message;
    }

    public static ApplicationStatus resolveFromCode(String str) {
        ApplicationStatus ApplicationStatus = FORMAT_MAP_BY_CODE.get(str);
        if (ApplicationStatus != null) return ApplicationStatus;
        throw new IllegalArgumentException(str);
    }

    public static void setApplicationName(String name) {
        if (APPLICATION_NAME == null) APPLICATION_NAME = name;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.format("%s_%s", APPLICATION_NAME, this.code);
    }
}
