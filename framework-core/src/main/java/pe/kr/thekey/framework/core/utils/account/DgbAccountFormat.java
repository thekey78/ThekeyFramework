package pe.kr.thekey.framework.core.utils.account;

public class DgbAccountFormat extends AccountFormat {
    public DgbAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{2})(\\d{11})", "$1-$2");
            case 14 -> {
                if (accountNumber.startsWith("937"))
                    yield accountNumber.replaceAll("(\\d{3})(\\d{11})", "$1-$2");
                else
                    yield accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})(\\d{3})", "$1-$2-$3-$4");
            }
            default -> accountNumber;
        });
    }
}
