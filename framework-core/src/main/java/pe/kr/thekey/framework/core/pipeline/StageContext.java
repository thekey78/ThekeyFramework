package pe.kr.thekey.framework.core.pipeline;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.kr.thekey.framework.core.context.RequestContext;

public interface StageContext {
    HttpServletRequest request();
    HttpServletResponse response();
    RequestContext requestContext();
    AttributeBag attributes();
}
