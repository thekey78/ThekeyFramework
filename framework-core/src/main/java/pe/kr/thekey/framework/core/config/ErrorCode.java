package pe.kr.thekey.framework.core.config;

public enum ErrorCode {
    FEX014("통신장애가 발생하였습니다. [수신중에러]"),
    FEX015("통신장애가 발생하였습니다. [기타오류]")
    ;

    private final String message;
    ErrorCode(String message) {
        this.message = message;
    }

    public String getCode() {
        return name();
    }

    public String getMessage() {
        return message;
    }
}
