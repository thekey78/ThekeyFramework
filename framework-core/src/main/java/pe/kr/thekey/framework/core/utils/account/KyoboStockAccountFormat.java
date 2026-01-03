package pe.kr.thekey.framework.core.utils.account;

public class KyoboStockAccountFormat extends AccountFormat {
    public KyoboStockAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 11) {
                return switch (accountNumber.substring(3, 5)) {
                    case "01", "31", "35", "51", "53", "54", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "71", "80"
                            -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
                    default -> accountNumber.replaceAll("(\\d{4})(\\d{4})(\\d{2})", "$1-$2-$3");
                };
            }
            return accountNumber;
        });
    }
}
