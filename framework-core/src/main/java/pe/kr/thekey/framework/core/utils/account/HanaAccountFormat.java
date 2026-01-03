package pe.kr.thekey.framework.core.utils.account;

public class HanaAccountFormat extends AccountFormat {
    public HanaAccountFormat() {
        // 005
        super(accountNumber -> switch (accountNumber.trim().length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{6})(\\d{3})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
