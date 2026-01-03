package pe.kr.thekey.framework.core.utils.account;

public class ImStockAccountFormat extends AccountFormat {
    public ImStockAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 10)
                return accountNumber.replaceAll("(\\d{4})(\\d{4})(\\d{2})", "$1-$2-$3");
            return accountNumber;
        });
    }
}
