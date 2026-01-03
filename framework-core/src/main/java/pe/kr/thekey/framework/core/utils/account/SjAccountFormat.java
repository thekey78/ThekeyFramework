package pe.kr.thekey.framework.core.utils.account;

public class SjAccountFormat extends AccountFormat {
    public SjAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{5})(\\d{2})(\\d{6})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
