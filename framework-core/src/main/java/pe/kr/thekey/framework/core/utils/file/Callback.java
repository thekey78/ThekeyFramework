package pe.kr.thekey.framework.core.utils.file;

@FunctionalInterface
public interface Callback<Argument1, Argument2> {
    void accept(Argument1 argument1, Argument2 argument2);
}
