package pe.kr.thekey.framework.core.utils.account;

public class MireAssetStockAccountFormat extends AccountFormat {
    public MireAssetStockAccountFormat() {
        super(accountNumber -> {
            if (accountNumber.length() == 11) {
                switch (accountNumber.substring(3, 5)) {
                    case "01", "99", "31", "44", "46", "51", "77" -> {
                        return accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{6})", "$1-$2-$3");
                    }
                }
            } else if (accountNumber.length() == 12) {
                switch (accountNumber.substring(3, 5)) {
                    case "20", "21", "22", "33", "39", "51", "58", "05", "15", "34", "37", "60", "62", "63", "90", "91", "92" -> {
                        return accountNumber.replaceAll("(\\d{3})(\\d{2})(\\d{7})", "$1-$2-$3");
                    }
                }
            }
            return accountNumber;
        });
    }
}
