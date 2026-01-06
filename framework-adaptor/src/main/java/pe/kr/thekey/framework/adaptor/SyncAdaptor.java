package pe.kr.thekey.framework.adaptor;

public interface SyncAdaptor {
    <Request, Response> Response execute(Request request);
}
