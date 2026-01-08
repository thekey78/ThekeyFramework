package pe.kr.thekey.framework.core.config;

import lombok.Getter;

public enum ErrorCode {
    FEX001("시스템구분 코드가 입력되지 않았습니다. I(내부), E(외부)에 대한 코드를 설정하세요."),
    FEX002("잘못된 시스템 구분 코드입니다.I(내부), E(외부)에 대한 코드를 설정하세요."),
    FEX003("내/외부시스템 식별자가 입력되지 않았습니다."),
    FEX004("글로벌ID가 입력되지 않았습니다."),
    FEX005("채널ID가 입력되지 않았습니다."),
    FEX006("시스템 식별코드가 등록되지 않았습니다."),
    FEX007("시스템 상세 식별코드가 등록되지 않았습니다."),
    FEX010("external 정보가 등록되지 않았습니다."),
    FEX011("Route 정보를 찾을수 없습니다."),
    FEX012("통신장애가 발생하였습니다. [소켓연결에러]"),
    FEX013("통신장애가 발생하였습니다. [송신중에러]"),
    FEX014("통신장애가 발생하였습니다. [수신중에러]"),
    FEX015("통신장애가 발생하였습니다. [기타오류]"),
    FEX016("수신 데이터 길이 초과"),
    FEX017("최대 세션정보가 초과 되었습니다. 현재 접속된 세션 수 ${0}"),
    FEX018("데이터 변환 오류. 입력된 데이터가 Serializable이 아닙니다. ${0}"),
    FEX019("데이터 형변환 오류. ${0}")
    ;

    @Getter
    private final String message;
    ErrorCode(String message) {
        this.message = message;
    }

    public String getCode() {
        return name();
    }
}
