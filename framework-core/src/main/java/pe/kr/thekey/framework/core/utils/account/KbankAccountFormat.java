package pe.kr.thekey.framework.core.utils.account;

public class KbankAccountFormat extends AccountFormat {
    public KbankAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> accountNumber.replaceAll("(\\d{1})(\\d{9})", "$1-$2");
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{6})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{2})(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3-$4");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{4})(\\d{3})(\\d{4})", "$1-$2-$3-$4");
            default -> accountNumber;
        });
    }
}
