package pe.kr.thekey.framework.core.utils.account;

public class KakaoAccountFormat extends AccountFormat {
    public KakaoAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 13)
                return accountNumber.replaceAll("(\\d{4})(\\d{2})(\\d{7})", "$1-$2-$3");
            return accountNumber;
        });
    }
}
