package pe.kr.thekey.framework.core.context;

public interface RequestContextHolder {
    RequestContext getRequired();
    RequestContext getOrNull();
    void set(RequestContext ctx);
    void clear();

    default RequestContext get() {
        return getOrNull();
    }
}
