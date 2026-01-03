package pe.kr.thekey.framework.core.utils.account;

public class JejuAccountFormat extends AccountFormat {
    public JejuAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> accountNumber.replaceAll("(\\d{2})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{7})", "$1-$2");
            default -> accountNumber;
        });
    }
}
