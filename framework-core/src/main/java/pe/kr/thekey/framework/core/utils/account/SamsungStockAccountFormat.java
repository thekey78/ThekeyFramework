package pe.kr.thekey.framework.core.utils.account;

public class SamsungStockAccountFormat extends AccountFormat {
    public SamsungStockAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 14) {
                return accountNumber.replaceAll("(\\d{1})(\\d{5})(\\d{8})", "$1-$2-$3");
            }
            return accountNumber;
        });
    }
}
