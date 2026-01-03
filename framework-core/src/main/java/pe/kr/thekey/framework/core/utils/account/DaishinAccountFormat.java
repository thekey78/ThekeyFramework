package pe.kr.thekey.framework.core.utils.account;

public class DaishinAccountFormat extends AccountFormat {
    public DaishinAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 9 -> accountNumber.replaceAll("(\\d{3})(\\d{6})", "$1-$2");
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{6})(\\d{2})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
