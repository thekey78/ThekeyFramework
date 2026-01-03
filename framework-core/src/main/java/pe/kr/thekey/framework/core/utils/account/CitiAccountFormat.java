package pe.kr.thekey.framework.core.utils.account;

public class CitiAccountFormat extends AccountFormat {
    public CitiAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 10 -> {
                if (accountNumber.charAt(0) == '5')
                    yield accountNumber.replaceAll("(\\d{1})(\\d{6})(\\d{3})", "$1-$2-$3");
                else
                    yield accountNumber.replaceAll("(\\d{2})(\\d{2})(\\d{6})", "$1-$2-$3");
            }
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{5})(\\d{3})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{1})(\\d{7})(\\d{2})(\\d{2})", "$1-$2-$3-$4");
            case 13 -> accountNumber.replaceAll("(\\d{3})(\\d{5})(\\d{3})(\\d{2})", "$1-$2-$3-$4");
            default -> accountNumber;
        });
    }
}
