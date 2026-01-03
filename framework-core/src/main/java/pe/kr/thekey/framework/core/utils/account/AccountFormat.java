package pe.kr.thekey.framework.core.utils.account;

import java.util.function.Function;

public class AccountFormat {
    Function<String, String> formatter;
    public AccountFormat(Function<String, String> formatter) {
        this.formatter = formatter;
    }
    public String format(String accountNumber) {
        return formatter.apply(clean(accountNumber));
    }

    protected String clean(String accountNumber) {
        if (accountNumber == null)
            return "";
        return accountNumber.trim();
    }
}
