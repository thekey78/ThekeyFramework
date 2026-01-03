package pe.kr.thekey.framework.core.utils.account;

public class SavingAccountFormat extends AccountFormat {
    public SavingAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 14) {
                return accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{2})(\\d{7})", "$1-$2-$3-$4");
            }
            return accountNumber;
        });
    }
}
