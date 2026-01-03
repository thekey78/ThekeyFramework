package pe.kr.thekey.framework.core.utils.account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountFormatFactory {
    private static volatile AccountFormatFactory instance;

    private final Map<Integer, AccountFormat> accountFormatMap;

    private AccountFormatFactory() {
        accountFormatMap = new ConcurrentHashMap<>();
        init();
    }

    private void init() {
        accountFormatMap.put(2, new KdbAccountFormat());//산업은행
        accountFormatMap.put(3, new IbkAccountFormat());//기업은행
        accountFormatMap.put(4, new KbAccountFormat());//국민은행
        accountFormatMap.put(5, new HanaAccountFormat());//하나은행
        accountFormatMap.put(6, new KbAccountFormat());//국민은행
        accountFormatMap.put(7, new SuhyupAccountFormat());//수협은행
        accountFormatMap.put(9, new SuhyupAccountFormat());//수협은행
        accountFormatMap.put(10, new NhAccountFormat());//NH 농협은행
        accountFormatMap.put(11, new NhAccountFormat());//NH 농협은행
        accountFormatMap.put(12, new NhAccountFormat());//지역 농축협
        accountFormatMap.put(13, new NhAccountFormat());//지역 농축협
        accountFormatMap.put(14, new NhAccountFormat());//지역 농축협
        accountFormatMap.put(15, new NhAccountFormat());//지역 농축협
        accountFormatMap.put(16, new NhAccountFormat());//NH 농협은행
        accountFormatMap.put(17, new NhAccountFormat());//지역 농축협
        accountFormatMap.put(18, new NhAccountFormat());//지역 농축협
        accountFormatMap.put(19, new KbAccountFormat());//국민은행
        accountFormatMap.put(20, new WooriAccountFormat());//우리은행
        accountFormatMap.put(21, new ShinhanAccountFormat());//신한은행
        accountFormatMap.put(22, new WooriAccountFormat());//우리은행
        accountFormatMap.put(23, new ScAccountFormat());//SC 제일은행
        accountFormatMap.put(24, new WooriAccountFormat());//우리은행
        accountFormatMap.put(25, new HanaAccountFormat());//하나은행
        accountFormatMap.put(26, new ShinhanAccountFormat());//신한은행
        accountFormatMap.put(27, new CitiAccountFormat());//한국씨티은행
        accountFormatMap.put(28, new ShinhanAccountFormat());//신한은행
        accountFormatMap.put(29, new KbAccountFormat());//국민은행
        accountFormatMap.put(30, new SuhyupAccountFormat());//수협중앙회 (미사용)
        accountFormatMap.put(31, new DgbAccountFormat());//대구은행
        accountFormatMap.put(32, new BusanAccountFormat());//부산은행
        accountFormatMap.put(33, new HanaAccountFormat());//하나은행
        accountFormatMap.put(34, new KjAccountFormat());//광주은행
        accountFormatMap.put(35, new JejuAccountFormat());//제주은행
        accountFormatMap.put(36, new CitiAccountFormat());//한국씨티은행
        accountFormatMap.put(37, new JbAccountFormat());//전북은행
        accountFormatMap.put(39, new KyongnamAccountFormat());//경남은행
        accountFormatMap.put(43, new IbkAccountFormat());//기업은행
        accountFormatMap.put(45, new MgAccountFormat());//새마을금고
        accountFormatMap.put(46, new MgAccountFormat());//새마을금고
        accountFormatMap.put(47, new CuAccountFormat());//신협
        accountFormatMap.put(48, new CuAccountFormat());//신협
        accountFormatMap.put(49, new CuAccountFormat());//신협
        accountFormatMap.put(50, new SavingAccountFormat());//저축은행
        accountFormatMap.put(52, new DefaultAccountFormat());//모간스탠리은행
        accountFormatMap.put(53, new CitiAccountFormat());//한국씨티은행
        accountFormatMap.put(54, new HsbcAccountFormat());//HSBC 은행
        accountFormatMap.put(55, new DefaultAccountFormat());//도이치은행
        accountFormatMap.put(57, new DefaultAccountFormat());//제이피모간체이스은행
        accountFormatMap.put(60, new BoaAccountFormat());//BOA 은행
        accountFormatMap.put(61, new BnpAccountFormat());//비엔피파리바은행
        accountFormatMap.put(64, new SjAccountFormat());//산림조합중앙회
        accountFormatMap.put(71, new PostAccountFormat());//우체국
        accountFormatMap.put(72, new PostAccountFormat());//우체국
        accountFormatMap.put(73, new PostAccountFormat());//우체국
        accountFormatMap.put(74, new PostAccountFormat());//우체국
        accountFormatMap.put(75, new PostAccountFormat());//우체국
        accountFormatMap.put(78, new KbAccountFormat());//국민은행
        accountFormatMap.put(79, new KbAccountFormat());//국민은행
        accountFormatMap.put(80, new HanaAccountFormat());//하나은행
        accountFormatMap.put(81, new HanaAccountFormat());//하나은행
        accountFormatMap.put(82, new HanaAccountFormat());//하나은행
        accountFormatMap.put(83, new WooriAccountFormat());//우리은행
        accountFormatMap.put(84, new WooriAccountFormat());//우리은행
        accountFormatMap.put(85, new MgAccountFormat());//새마을금고
        accountFormatMap.put(86, new MgAccountFormat());//새마을금고
        accountFormatMap.put(87, new MgAccountFormat());//새마을금고
        accountFormatMap.put(88, new ShinhanAccountFormat());//신한은행
        accountFormatMap.put(89, new KbankAccountFormat());//케이뱅크
        accountFormatMap.put(90, new KakaoAccountFormat());//카카오뱅크
        accountFormatMap.put(92, new TossAccountFormat());//토스뱅크
        accountFormatMap.put(209, new YuantaStockAccountFormat());	//유안타증권
        accountFormatMap.put(218, new KbStockAccountFormat());	//KB 증권
//        accountFormatMap.put(221, new DefaultAccountFormat());	//상상인증권
//        accountFormatMap.put(222, new DefaultAccountFormat());	//한양증권
//        accountFormatMap.put(223, new DefaultAccountFormat());	//리딩투자증권
//        accountFormatMap.put(224, new DefaultAccountFormat());	//BNK 투자증권
//        accountFormatMap.put(225, new DefaultAccountFormat());	//IBK 투자증권
//        accountFormatMap.put(226, new KbStockAccountFormat());	//KB 증권
//        accountFormatMap.put(227, new DefaultAccountFormat());	//KTB 투자증권
        accountFormatMap.put(230, new MireAssetStockAccountFormat());	//미래에셋증권
        accountFormatMap.put(238, new MireAssetStockAccountFormat());	//미래에셋증권
        accountFormatMap.put(240, new SamsungStockAccountFormat());	//삼성증권
        accountFormatMap.put(243, new KoreaInvestmentStockAccountFormat());	//한국투자증권
        accountFormatMap.put(247, new DefaultAccountFormat());	//NH 투자증권
        accountFormatMap.put(261, new KyoboStockAccountFormat());	//교보증권
        accountFormatMap.put(262, new ImStockAccountFormat());	//아이엠투자증권
        accountFormatMap.put(263, new DefaultAccountFormat());	//현대차증권
        accountFormatMap.put(264, new KiwoomStockAccountFormat());	//키움증권
        accountFormatMap.put(265, new LsStockAccountFormat());	//LS증권, 이베스트투자증권
        accountFormatMap.put(266, new DefaultAccountFormat());	//SK 증권
        accountFormatMap.put(267, new DaishinAccountFormat());	//대신증권
        accountFormatMap.put(268, new DefaultAccountFormat());	//메리츠증권
        accountFormatMap.put(269, new HanhwaStockAccountFormat());	//한화투자증권
        accountFormatMap.put(270, new HanaStockAccountFormat());	//하나금융투자
        accountFormatMap.put(271, new DefaultAccountFormat());	//토스증권
        accountFormatMap.put(272, new DefaultAccountFormat());	//NH 선물
        accountFormatMap.put(278, new ShinhanStockAccountFormat());	//신한금융투자
        accountFormatMap.put(279, new DefaultAccountFormat());	//DB 금융투자
        accountFormatMap.put(280, new DefaultAccountFormat());	//유진투자증권
        accountFormatMap.put(287, new MeritzStockAccountFormat());	//메리츠증권
        accountFormatMap.put(288, new DefaultAccountFormat());	//카카오페이증권
        accountFormatMap.put(289, new DefaultAccountFormat());	//NH 투자증권
        accountFormatMap.put(290, new BookookStockAccountFormat());	//부국증권
        accountFormatMap.put(291, new DefaultAccountFormat());	//신영증권
        accountFormatMap.put(292, new CapefnAccountFormat());	//케이프투자증권
        accountFormatMap.put(293, new DefaultAccountFormat());	//한국증권금융
        accountFormatMap.put(294, new DefaultAccountFormat());	//한국포스증권
        accountFormatMap.put(295, new WooriStockAccountFormat());	//우리종합금융


    }


    public static AccountFormatFactory getInstance() {
        if (instance == null) {
            synchronized (AccountFormatFactory.class) {
                if (instance == null) {
                    instance = new AccountFormatFactory();
                }
            }
        }
        return instance;
    }

    public AccountFormat getAccountFormat(int bankCode) {
        return accountFormatMap.getOrDefault(bankCode, new DefaultAccountFormat());
    }
}
