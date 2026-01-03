package pe.kr.thekey.framework.core.utils.account;

public class BnpAccountFormat extends AccountFormat {
    public BnpAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 14)
                return accountNumber.replaceAll("(\\d{5})(\\d{6})(\\d{3})", "$1-$2-$3");
            return accountNumber;
        });
    }
}
