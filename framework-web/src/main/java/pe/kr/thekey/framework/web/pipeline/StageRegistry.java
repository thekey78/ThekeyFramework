package pe.kr.thekey.framework.web.pipeline;

import pe.kr.thekey.framework.core.pipeline.Stage;

import java.util.*;

public final class StageRegistry {
    private final Map<String, Stage> stages;

    public StageRegistry(Collection<Stage> stageBeans) {
        Map<String, Stage> m = new HashMap<>();
        for (Stage s : stageBeans) {
            if (m.containsKey(s.id())) throw new IllegalStateException("Duplicate stage id: " + s.id());
            m.put(s.id(), s);
        }
        this.stages = Collections.unmodifiableMap(m);
    }

    public Stage getRequired(String id) {
        Stage s = stages.get(id);
        if (s == null) throw new IllegalStateException("Stage not found: " + id);
        return s;
    }

    public boolean contains(String id) { return stages.containsKey(id); }
    public Set<String> ids() { return stages.keySet(); }

}
