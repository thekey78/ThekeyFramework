package pe.kr.thekey.framework.core.utils.account;

public class PostAccountFormat extends AccountFormat {
    public PostAccountFormat() {
        super(accountNumber -> switch (accountNumber.length()) {
            case 12 -> accountNumber.replaceAll("(\\d{3})(\\d{9})", "$1-$2");
            case 13 -> {
                if (accountNumber.charAt(0) == '8' || accountNumber.charAt(0) == '9')
                    yield accountNumber.replaceAll("(\\d{1})(\\d{12})", "$1-$2");
                else
                    yield accountNumber.replaceAll("(\\d{6})(\\d{7})", "$1-$2");
            }
            case 14 -> accountNumber.replaceAll("(\\d{6})(\\d{2})(\\d{6})", "$1-$2-$3");
            default -> accountNumber;
        });
    }
}
