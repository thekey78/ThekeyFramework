package pe.kr.thekey.framework.starter.web;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class SpringRequestAttributesProvider implements DefaultRequestContextHolder.RequestAttributesProvider{
    @Override
    public RequestAttributes getOrNull() {
        return RequestContextHolder.getRequestAttributes();
    }
}
