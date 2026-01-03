package pe.kr.thekey.framework.core.utils.account;

public class WooriAccountFormat extends AccountFormat {
    //020
    public WooriAccountFormat() {
        super((accountNumber) -> switch (accountNumber.length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{4})(\\d{3})(\\d{6})", "$1-$2-$3");
            case 14 -> accountNumber.replaceAll("(\\d{3})(\\d{6})(\\d{2})(\\d{3})", "$1-$2-$3-$4");
            default -> accountNumber;
        });
    }
}
