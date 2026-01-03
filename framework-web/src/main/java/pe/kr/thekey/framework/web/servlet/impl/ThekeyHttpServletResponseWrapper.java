package pe.kr.thekey.framework.web.servlet.impl;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletResponse;

public class ThekeyHttpServletResponseWrapper extends HttpServletResponseWrapper implements ThekeyHttpServletResponse {
    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the {@link HttpServletResponse} to be wrapped.
     *
     * @throws IllegalArgumentException if the response is null
     */
    public ThekeyHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }
}
