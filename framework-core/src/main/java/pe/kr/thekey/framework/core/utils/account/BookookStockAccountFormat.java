package pe.kr.thekey.framework.core.utils.account;

public class BookookStockAccountFormat extends AccountFormat {
    public BookookStockAccountFormat() {
        super(accountNumber -> {
            if(accountNumber.length() == 11)
                return accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            return accountNumber;
        });
    }
}
