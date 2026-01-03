package pe.kr.thekey.framework.core.utils.account;

public class KdbAccountFormat extends AccountFormat {
    //"002"
    public KdbAccountFormat() {
        super(accountNumber -> switch (accountNumber.trim().length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{5})(\\d{2})", "$1-$2-$3-$4");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{8})(\\d{3})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
