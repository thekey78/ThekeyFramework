package pe.kr.thekey.framework.web.servlet;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface ThekeyHttpServletRequest extends HttpServletRequest {
    void replaceParameter(String name, String... value);

    void removeParameter(String name);

    Map<String, Object> getAttributesMap();
}
