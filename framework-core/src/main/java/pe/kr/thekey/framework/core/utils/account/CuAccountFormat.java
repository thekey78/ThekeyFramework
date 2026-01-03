package pe.kr.thekey.framework.core.utils.account;

public class CuAccountFormat extends AccountFormat {
    public CuAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{3})(\\d{6})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{5})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 14 -> accountNumber.replaceAll("(\\d{5})(\\d{2})(\\d{7})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
