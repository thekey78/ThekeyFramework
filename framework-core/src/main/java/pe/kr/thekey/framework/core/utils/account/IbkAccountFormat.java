package pe.kr.thekey.framework.core.utils.account;

/**
 * 기업은행 계좌번호 포맷팅
 */
public class IbkAccountFormat extends AccountFormat {
    public IbkAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> accountNumber.replaceAll("(\\d{8})(\\d{2})", "$1-$2");
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{8})", "$1-$2");
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{6})(\\d{2})(\\d{3})", "$1-$2-$3-$4");
            default -> accountNumber;
        });
    }
}
