package net.shadew.asm.mappings.visit;

import net.shadew.asm.mappings.model.FieldMapping;
import net.shadew.asm.mappings.model.LvtMapping;
import net.shadew.asm.mappings.model.MethodMapping;
import net.shadew.asm.mappings.model.TypeMapping;

public interface MappingsConsumer {
    void visitType(TypeMapping mapping);
    void visitField(FieldMapping mapping);
    void visitMethod(MethodMapping mapping);
    void visitLvt(LvtMapping mapping);
}
