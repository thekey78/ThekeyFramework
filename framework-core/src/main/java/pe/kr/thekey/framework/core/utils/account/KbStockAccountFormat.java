package pe.kr.thekey.framework.core.utils.account;

public class KbStockAccountFormat extends AccountFormat {
    public KbStockAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 11 -> switch (accountNumber.substring(3, 5)) {
                case "01", "06", "07", "10", "11", "12", "16", "30", "40", "45", "50", "55", "61", "62", "63", "64", "65", "66"
                        -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
                default -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{5})", "$1-$2-$3");
            };
            case 9 -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
