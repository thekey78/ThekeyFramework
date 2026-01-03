package pe.kr.thekey.framework.core.utils.account;

public class TossAccountFormat extends AccountFormat {
    public TossAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 12) {
                if (accountNumber.startsWith("17") || accountNumber.startsWith("19"))
                    return accountNumber.replaceAll("(\\d{2})(\\d{10})", "$1-$2");
                return accountNumber.replaceAll("(\\d{3})(\\d{8})(\\d{1})", "$1-$2-$3");
            }
            return accountNumber;
        });
    }
}
