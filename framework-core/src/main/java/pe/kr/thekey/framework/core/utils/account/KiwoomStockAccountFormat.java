package pe.kr.thekey.framework.core.utils.account;

public class KiwoomStockAccountFormat extends AccountFormat {
    public KiwoomStockAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 8 -> accountNumber.replaceAll("(\\d{4})(\\d{4})", "$1-$2");
            case 10 -> accountNumber.replaceAll("(\\d{4})(\\d{4})(\\d{2})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
