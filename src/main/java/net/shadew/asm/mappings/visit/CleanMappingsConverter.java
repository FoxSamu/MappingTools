package net.shadew.asm.mappings.visit;

import net.shadew.asm.mappings.model.*;

public class CleanMappingsConverter extends MappingsConverter {
    public CleanMappingsConverter() {
    }

    public CleanMappingsConverter(Mappings mappings) {
        super(mappings);
    }

    @Override
    protected TypeMapping convertType(TypeMapping mapping, Mappings newParent) {
        return newParent.newType(mapping.name());
    }

    @Override
    protected FieldMapping convertField(FieldMapping mapping, TypeMapping newParent) {
        return newParent.newField(mapping.name());
    }

    @Override
    protected MethodMapping convertMethod(MethodMapping mapping, TypeMapping newParent) {
        return newParent.newMethod(mapping.name(), mapping.desc());
    }

    @Override
    protected LvtMapping convertLvt(LvtMapping mapping, MethodMapping newParent) {
        return newParent.newLvt(mapping.index(), mapping.name(), mapping.desc());
    }
}
