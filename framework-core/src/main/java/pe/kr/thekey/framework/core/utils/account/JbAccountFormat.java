package pe.kr.thekey.framework.core.utils.account;

public class JbAccountFormat extends AccountFormat {
    public JbAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{1})(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3-$4");
            default -> accountNumber;
        });
    }
}
