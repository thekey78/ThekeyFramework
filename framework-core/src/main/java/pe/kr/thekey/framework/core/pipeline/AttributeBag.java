package pe.kr.thekey.framework.core.pipeline;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeBag {
    private final Map<String, Object> map = new ConcurrentHashMap<>();
    public void put(String k, Object v) { map.put(k, v); }
    public Object get(String k) { return map.get(k); }
    public <T> T get(String k, Class<T> type) { return type.cast(map.get(k)); }
    public Map<String, Object> asMap() { return map; }

    @Override
    public String toString() {
        return map.toString();
    }
}
