package net.shadew.asm.mappings.model;

public interface Mapping {
    String name();

    String get();
    void set(String to);
    void clear();

    default String remap() {
        String out = get();
        return out == null ? name() : out;
    }
}
