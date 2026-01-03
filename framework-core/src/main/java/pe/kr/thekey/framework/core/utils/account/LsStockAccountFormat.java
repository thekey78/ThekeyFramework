package pe.kr.thekey.framework.core.utils.account;

public class LsStockAccountFormat extends AccountFormat {
    public LsStockAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 11) {
                return switch (accountNumber.substring(3,5)) {
                    case "01", "11", "45", "51", "55", "56", "65", "77", "78"
                            -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
                    default -> accountNumber;
                };
            }
            return accountNumber;
        });
    }
}
