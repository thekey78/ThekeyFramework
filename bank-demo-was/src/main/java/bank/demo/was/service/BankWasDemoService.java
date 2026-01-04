package bank.demo.was.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletRequest;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BankWasDemoService {
    public Map<String, Object> getBankInfo(ThekeyHttpServletRequest request) {
        return Map.of("bankName", "Bank Was");
    }

    public String getBankName() {
        return "Bank Was";
    }
}
