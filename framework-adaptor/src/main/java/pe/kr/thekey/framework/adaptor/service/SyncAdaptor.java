package pe.kr.thekey.framework.adaptor.service;

public interface SyncAdaptor {
    <Request, Response> Response execute(Request request);
}
