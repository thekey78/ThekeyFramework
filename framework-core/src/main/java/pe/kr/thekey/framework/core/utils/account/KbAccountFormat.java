package pe.kr.thekey.framework.core.utils.account;

/**
 * 기업은행 계좌번호 포맷팅
 */
public class KbAccountFormat extends AccountFormat {
    //"004", "006"
    public KbAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
            case 11 -> {
                if (accountNumber.charAt(0) == '9')
                    yield accountNumber;
                else
                    yield accountNumber.replaceAll("(\\d{3})(\\d{4})(\\d{3})", "$1-$2-$3");
            }
            case 12 -> {
                if (accountNumber.startsWith("92", 4))
                    yield accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{4})(\\d{3})", "$1-$2-$3-$4");
                else
                    yield accountNumber.replaceAll("(\\d{4})(\\d{2})(\\d{6})", "$1-$2-$3");
            }
            case 14 -> accountNumber.replaceAll("(\\d{4})(\\d{2})(\\d{8})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
