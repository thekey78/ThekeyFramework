package pe.kr.thekey.framework.adaptor.service;

import pe.kr.thekey.framework.core.utils.Callback;

public interface AsyncAdaptor {
    <Request, Response> void execute(Request request, Callback<Request, Response> callback);
}
