package pe.kr.thekey.framework.adaptor.exception;

import pe.kr.thekey.framework.core.config.ErrorCode;

public class ExternalException extends RuntimeException {
    public ExternalException(ErrorCode errorCode) {
        super("["+errorCode.getCode()+"] " + errorCode.getMessage());
    }
    public ExternalException(ErrorCode errorCode, Throwable cause) {
        super("["+errorCode.getCode()+"] " + errorCode.getMessage(), cause);
    }

    public ExternalException(ErrorCode errorCode, String message) {
        super("["+errorCode.getCode()+"] " + message);
    }

    public ExternalException(ErrorCode errorCode, String message, Throwable cause) {
        super("["+errorCode.getCode()+"] " + message, cause);
    }
}
