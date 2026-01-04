package pe.kr.thekey.framework.starter.web;

import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletRequest;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DefaultRequestContext implements RequestContext {
    protected final ThekeyHttpServletRequest request;
    public DefaultRequestContext(ThekeyHttpServletRequest request) {
        this.request = request;
        this.request.setAttribute("requestContext", this);
    }

    @Override
    public String requestId() {
        return request.getRequestId();
    }

    @Override
    public String traceId() {
        return request.getHeader("X-Trace-Id");
    }

    @Override
    public String channel() {
        return request.getHeader("X-Channel");
    }

    @Override
    public String apiVersion() {
        return request.getHeader("X-Api-Version");
    }

    @Override
    public String clientIp() {
        return request.getRemoteAddr();
    }

    @Override
    public String userAgent() {
        return request.getHeader("User-Agent");
    }

    @Override
    public String principalId() {
        return request.getHeader("X-Principal-Id");
    }

    @Override
    public Map<String, Object> attributes() {
        return request.getAttributesMap();
    }

    @Override
    public Map<String, String[]> parameters() {
        return request.getParameterMap();
    }

    @Override
    public Map<String, String> headers() {
        Iterable<String> iterable = () -> request.getHeaderNames().asIterator();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toMap(e -> e, request::getHeader));
    }
}
