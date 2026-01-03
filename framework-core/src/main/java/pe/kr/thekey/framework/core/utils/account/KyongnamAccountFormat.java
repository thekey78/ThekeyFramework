package pe.kr.thekey.framework.core.utils.account;

public class KyongnamAccountFormat extends AccountFormat {
    //039
    public KyongnamAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{3})(\\d{10})", "$1-$2");
            default -> accountNumber;
        });
    }
}
