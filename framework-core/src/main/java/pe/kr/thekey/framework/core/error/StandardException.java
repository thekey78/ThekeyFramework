package pe.kr.thekey.framework.core.error;

import lombok.Getter;

@Getter
public class StandardException extends RuntimeException {
    private final String code;
    private final String message;
    private final String traceId;
    private final String requestId;
    private int statusCode = 500;

    public StandardException(String code) {
        this(code, null, null, null);
    }
    public StandardException(String code, int statusCode) {
        this(code, null, null, null, statusCode);
    }

    public StandardException(String code, String message) {
        this(code, message, null, null);
    }
    public StandardException(String code, String message, int statusCode) {
        this(code, message, null, null, statusCode);
    }

    public StandardException(String code, Throwable cause) {
        this(code, cause.getMessage(), null, null, 500, cause);
    }

    public StandardException(String code, int statusCode, Throwable cause) {
        this(code, cause.getMessage(), null, null, statusCode, cause);
    }

    public StandardException(String code, String message, Throwable cause) {
        this(code, message, null, null, 500, cause);
    }

    public StandardException(String code, String message, String traceId, String requestId) {
        this(code, message, traceId, requestId, 500);
    }

    public StandardException(String code, String message, String traceId, String requestId, Throwable cause) {
        this(code, message, traceId, requestId, 500, cause);
    }

    public StandardException(String code, String message, String traceId, String requestId, int statusCode) {
        this(code, message, traceId, requestId, statusCode, null);
    }

    public StandardException(String code, String message, String traceId, String requestId, int statusCode, Throwable cause) {
        super("["+code+"]" + message, cause);
        this.code = code;
        this.message = message;
        this.traceId = traceId;
        this.requestId = requestId;
        this.statusCode = statusCode;
    }
}
