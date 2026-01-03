package pe.kr.thekey.framework.core.context;

import java.util.Map;

public interface RequestContext {
    String requestId();
    String traceId();
    String channel();        // APP/WEB/BATCH 등
    String apiVersion();

    String clientIp();
    String userAgent();

    String principalId();    // 식별자만(민감정보 금지)
    Map<String, Object> attributes();
    Map<String, String[]> parameters();
    Map<String, String> headers();

    enum TokenStatus { NONE, VALID, INVALID, EXPIRED, REPLAY }
}
