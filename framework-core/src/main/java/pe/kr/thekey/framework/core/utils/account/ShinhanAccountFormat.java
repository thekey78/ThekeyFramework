package pe.kr.thekey.framework.core.utils.account;

public class ShinhanAccountFormat extends AccountFormat {
    public ShinhanAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{9})", "$1-$2");
            case 13 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{8})", "$1-$2-$3");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{8})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
