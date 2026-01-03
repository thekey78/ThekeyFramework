package pe.kr.thekey.framework.core.utils;

import pe.kr.thekey.framework.core.utils.account.*;

public class FormatUtils {
    public static String accountFormat(int bankCode, String accountNumber) {
        return AccountFormatFactory.getInstance().getAccountFormat(bankCode).format(accountNumber);
    }
}