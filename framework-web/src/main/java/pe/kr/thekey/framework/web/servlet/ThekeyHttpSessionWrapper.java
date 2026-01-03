package pe.kr.thekey.framework.web.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import pe.kr.thekey.framework.core.utils.ApplicationContextHolder;
import pe.kr.thekey.framework.web.property.FrameworkWebProperties;

import java.io.Serializable;
import java.util.Enumeration;

public class ThekeyHttpSessionWrapper implements HttpSession {
    private final HttpSession session;
    public ThekeyHttpSessionWrapper(HttpSession session) {
        this.session = session;
    }

    @Override
    public long getCreationTime() {
        return session.getCreationTime();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    @Override
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return session.getAttributeNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value instanceof Serializable) {
            FrameworkWebProperties bean = ApplicationContextHolder.getApplicationContext().getBean(FrameworkWebProperties.class);
            if (bean.getSessionKey().isEnabled()) {
                if (bean.getSessionKey().getKeys().contains(name)) {
                    session.setAttribute(name, value);
                }
                else {
                    throw new IllegalArgumentException("Session key not allowed: " + name);
                }
            }
            else {
                session.setAttribute(name, value);
            }
        }
        else {
            throw new IllegalArgumentException("Session value must be serializable: " + value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    @Override
    public void invalidate() {
        session.invalidate();
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }

    @Override
    public Accessor getAccessor() {
        return session.getAccessor();
    }
}
