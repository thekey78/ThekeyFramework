package bank.demo.common.stage;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.pipeline.StageContext;

import java.util.UUID;

@Slf4j
public class ResponseDdosTokenStage implements Stage {
    @Override
    public String id() {
        return "token-make";
    }

    @Override
    public Phase phase() {
        return Phase.OUTPUT;
    }

    @Override
    public void execute(StageContext ctx) {
        log.debug("Executing DDOS Token Generation Stage");

        ctx.request().getSession().removeAttribute(Constants.DDOS_TOKEN_NAME);

        String uuid = UUID.randomUUID().toString();
        ctx.response().setHeader(Constants.DDOS_TOKEN_NAME, uuid);
        ctx.response().addCookie(makeCookie(Constants.DDOS_TOKEN_NAME, uuid));
        ctx.request().getSession().setAttribute(Constants.DDOS_TOKEN_NAME, uuid);
    }

    private Cookie makeCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
