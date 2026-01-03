package pe.kr.thekey.framework.core.utils.account;

public class MeritzStockAccountFormat extends AccountFormat {
    public MeritzStockAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> accountNumber.replaceAll("(\\d{4})(\\d{4})(\\d{2})", "$1-$2-$3");
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
