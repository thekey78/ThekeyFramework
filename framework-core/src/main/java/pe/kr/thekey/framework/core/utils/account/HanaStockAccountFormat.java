package pe.kr.thekey.framework.core.utils.account;

public class HanaStockAccountFormat extends AccountFormat {
    public HanaStockAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 8 -> accountNumber.replaceAll("(\\d{7})(\\d{1})", "$1-$2");
            case 10 -> accountNumber.replaceAll("(\\d{7})(\\d{1})(\\d{2})", "$1-$2-$3");
            case 11 -> accountNumber.replaceAll("(\\d{8})(\\d{3})", "$1-$2");
            case 14 -> accountNumber.replaceAll("(\\d{8})(\\d{3})(\\d{3})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
