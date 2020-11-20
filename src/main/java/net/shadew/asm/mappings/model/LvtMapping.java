package net.shadew.asm.mappings.model;

public interface LvtMapping extends Mapping {
    MethodMapping parent();
    int index();
    String desc();

    default Mappings root() {
        return parent().parent().parent();
    }
}
