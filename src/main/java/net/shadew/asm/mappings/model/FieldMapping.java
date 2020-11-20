package net.shadew.asm.mappings.model;

public interface FieldMapping extends Mapping {
    TypeMapping parent();

    default Mappings root() {
        return parent().parent();
    }
}
