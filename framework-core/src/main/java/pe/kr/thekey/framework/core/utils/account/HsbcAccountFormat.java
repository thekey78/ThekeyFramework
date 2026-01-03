package pe.kr.thekey.framework.core.utils.account;

public class HsbcAccountFormat extends AccountFormat {
    public HsbcAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 12) {
                return accountNumber.replaceAll("(\\d{3})(\\d{6})(\\d{3})", "$1-$2-$3");
            }
            return accountNumber;
        });
    }
}
