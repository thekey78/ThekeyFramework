package pe.kr.thekey.framework.core.utils.account;

public class HanhwaStockAccountFormat extends AccountFormat {
    public HanhwaStockAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{8})", "$1-$2");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{11})", "$1-$2");
            default -> accountNumber;
        });
    }
}
