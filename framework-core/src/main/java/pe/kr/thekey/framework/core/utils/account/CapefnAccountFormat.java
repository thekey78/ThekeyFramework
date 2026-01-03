package pe.kr.thekey.framework.core.utils.account;

public class CapefnAccountFormat extends AccountFormat {
    public CapefnAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})(\\d{3})", "$1-$2-$3-$4");
            default -> accountNumber;
        });
    }
}
