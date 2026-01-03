package pe.kr.thekey.framework.core.utils.account;

public class KoreaInvestmentStockAccountFormat extends AccountFormat {
    public KoreaInvestmentStockAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> accountNumber.replaceAll("(\\d{8})(\\d{2})", "$1-$2");
            case 12 -> accountNumber.replaceAll("(\\d{8})(\\d{4})", "$1-$2");
            case 13 -> accountNumber.replaceAll("(\\d{8})(\\d{2})(\\d{4})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
