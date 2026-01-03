package pe.kr.thekey.framework.core.utils.account;

public class KjAccountFormat extends AccountFormat {
    //"034"
    public KjAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{6})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{1})(\\d{3})(\\d{9})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
