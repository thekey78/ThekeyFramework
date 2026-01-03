package pe.kr.thekey.framework.web.servlet.impl;

import jakarta.servlet.http.*;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletRequest;

import java.util.*;

public class ThekeyHttpServletRequestWrapper extends HttpServletRequestWrapper implements ThekeyHttpServletRequest {
    private final Map<String, String[]> parameters = new HashMap<>();
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * Wraps request; caches parameters and attributes
     */
    public ThekeyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

        if ( request.getParameterNames() != null)
            request.getParameterNames().asIterator().forEachRemaining(k -> parameters.put(k, request.getParameterValues(k)));

        if ( request.getAttributeNames() != null)
            request.getAttributeNames().asIterator().forEachRemaining(k -> attributes.put(k, request.getAttribute(k)));
    }

    @Override
    public String getParameter(String name) {
        if (parameters.get(name) == null) {
            return null;
        }
        else {
            return String.join(",", parameters.get(name));
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Map.copyOf(parameters);
    }

    @Override
    public void replaceParameter(String name, String... value) {
        parameters.replace(name, value);
    }

    @Override
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    @Override
    public Map<String, Object> getAttributesMap() {
        return Map.copyOf(attributes);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }
}
