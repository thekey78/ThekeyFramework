package pe.kr.thekey.framework.core.utils.account;

public class ScAccountFormat extends AccountFormat {
    //"023"
    public ScAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{9})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
