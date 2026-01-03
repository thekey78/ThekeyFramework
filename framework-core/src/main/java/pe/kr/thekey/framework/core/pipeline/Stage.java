package pe.kr.thekey.framework.core.pipeline;

public interface Stage {
    String id();
    Phase phase();
    void execute(StageContext ctx) ;
}
