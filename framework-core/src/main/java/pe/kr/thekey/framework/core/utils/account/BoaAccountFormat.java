package pe.kr.thekey.framework.core.utils.account;

public class BoaAccountFormat extends AccountFormat {
    public BoaAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 12 -> accountNumber.replaceAll("(\\d{4})(\\d{5})(\\d{2})(\\d{1})", "$1-$2-$3-$4");
            case 14 -> accountNumber.replaceAll("(\\d{4})(\\d{10})", "$1-$2");
            default -> accountNumber;
        });
    }
}
