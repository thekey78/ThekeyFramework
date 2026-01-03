package pe.kr.thekey.framework.core.utils.account;

public class YuantaStockAccountFormat extends AccountFormat {
    public YuantaStockAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{4})(\\d{4})(\\d{4})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
