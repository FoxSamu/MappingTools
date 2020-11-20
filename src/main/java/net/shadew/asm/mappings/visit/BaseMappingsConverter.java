package net.shadew.asm.mappings.visit;

import net.shadew.asm.mappings.model.*;

public class BaseMappingsConverter extends MappingsConverter {
    public BaseMappingsConverter() {
    }

    public BaseMappingsConverter(Mappings mappings) {
        super(mappings);
    }

    @Override
    protected TypeMapping convertType(TypeMapping mapping, Mappings newParent) {
        return newParent.newType(mapping.name(), mapping.get());
    }

    @Override
    protected FieldMapping convertField(FieldMapping mapping, TypeMapping newParent) {
        return newParent.newField(mapping.name(), mapping.get());
    }

    @Override
    protected MethodMapping convertMethod(MethodMapping mapping, TypeMapping newParent) {
        return newParent.newMethod(mapping.name(), mapping.desc(), mapping.get());
    }

    @Override
    protected LvtMapping convertLvt(LvtMapping mapping, MethodMapping newParent) {
        return newParent.newLvt(mapping.index(), mapping.name(), mapping.desc(), mapping.get());
    }
}
