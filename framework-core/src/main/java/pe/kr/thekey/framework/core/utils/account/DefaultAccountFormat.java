package pe.kr.thekey.framework.core.utils.account;

public class DefaultAccountFormat extends AccountFormat {
    public DefaultAccountFormat() {
        super(accountNumber -> accountNumber);
    }
}
