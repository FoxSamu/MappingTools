package net.shadew.asm.mappings.visit;

import net.shadew.asm.mappings.model.*;

public class MappingsRemapper implements MappingsConsumer {
    private final Mappings reference;

    public MappingsRemapper(Mappings reference) {
        this.reference = reference;
    }

    @Override
    public void visitType(TypeMapping mapping) {
        TypeMapping ref = reference.type(mapping.name());
        if (ref != null)
            mapping.set(ref.remap());
    }

    @Override
    public void visitField(FieldMapping mapping) {
        TypeMapping refType = reference.type(mapping.parent().name());
        if (refType != null) {
            FieldMapping ref = refType.field(mapping.name());
            if (ref != null)
                mapping.set(ref.remap());
        }
    }

    @Override
    public void visitMethod(MethodMapping mapping) {
        TypeMapping refType = reference.type(mapping.parent().name());
        if (refType != null) {
            MethodMapping ref = refType.method(mapping.name(), mapping.desc());
            if (ref != null)
                mapping.set(ref.remap());
        }
    }

    @Override
    public void visitLvt(LvtMapping mapping) {
        TypeMapping refType = reference.type(mapping.parent().parent().name());
        if (refType != null) {
            MethodMapping refMethod = refType.method(mapping.parent().name(), mapping.parent().desc());
            if (refMethod != null) {
                LvtMapping ref = refMethod.lvt(mapping.index(), mapping.desc());
                if (ref != null)
                    mapping.set(ref.remap());
            }
        }
    }
}
