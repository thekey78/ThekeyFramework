package pe.kr.thekey.framework.core.utils.account;

public class MgAccountFormat extends AccountFormat {
    //045
    public MgAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 13) {
                if (accountNumber.charAt(0) == '9')
                    return accountNumber.replaceAll("(\\d{4})(\\d{9})", "$1-$2");
                else
                    return accountNumber.replaceAll("(\\d{4})(\\d{2})(\\d{7})", "$1-$2-$3");
            }
            return accountNumber;
        });
    }
}
