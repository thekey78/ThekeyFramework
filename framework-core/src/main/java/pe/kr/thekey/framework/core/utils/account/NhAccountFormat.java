package pe.kr.thekey.framework.core.utils.account;

public class NhAccountFormat extends AccountFormat {
    //"011", "012"
    public NhAccountFormat() {
        super(accountNumber -> switch (accountNumber.trim().length()) {
            case 11 -> accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 12 -> accountNumber.replaceAll("(\\d{4})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 13 -> accountNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})(\\d{2})", "$1-$2-$3-$4");
            case 14 -> switch (accountNumber.substring(0, 3)) {
                case "790", "791", "792" -> accountNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})(\\d{3})", "$1-$2-$3-$4");
                default -> accountNumber.replaceAll("(\\d{6})(\\d{2})(\\d{6})", "$1-$2-$3");
            };
            default -> accountNumber;
        });
    }
}
