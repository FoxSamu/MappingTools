package net.shadew.asm.mappings.visit;

import net.shadew.asm.mappings.model.*;

public abstract class MappingsConverter implements MappingsConsumer {
    private final Mappings mappings;
    private TypeMapping type;
    private MethodMapping method;

    public MappingsConverter() {
        this(Mappings.create());
    }

    public MappingsConverter(Mappings mappings) {
        this.mappings = mappings;
    }

    public Mappings getMappings() {
        return mappings;
    }

    @Override
    public final void visitType(TypeMapping mapping) {
        type = convertType(mapping, mappings);
    }

    protected abstract TypeMapping convertType(TypeMapping mapping, Mappings newParent);

    @Override
    public final void visitField(FieldMapping mapping) {
        if (type == null) return;
        convertField(mapping, type);
    }

    protected abstract FieldMapping convertField(FieldMapping mapping, TypeMapping newParent);

    @Override
    public final void visitMethod(MethodMapping mapping) {
        if (type == null) return;
        method = convertMethod(mapping, type);
    }

    protected abstract MethodMapping convertMethod(MethodMapping mapping, TypeMapping newParent);

    @Override
    public final void visitLvt(LvtMapping mapping) {
        if (method == null) return;
        convertLvt(mapping, method);
    }

    protected abstract LvtMapping convertLvt(LvtMapping mapping, MethodMapping newParent);
}
