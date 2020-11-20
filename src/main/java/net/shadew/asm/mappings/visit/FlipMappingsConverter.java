package net.shadew.asm.mappings.visit;

import net.shadew.asm.mappings.model.*;

public class FlipMappingsConverter extends MappingsConverter {
    public FlipMappingsConverter() {
    }

    public FlipMappingsConverter(Mappings mappings) {
        super(mappings);
    }

    @Override
    protected TypeMapping convertType(TypeMapping mapping, Mappings newParent) {
        return newParent.newType(mapping.remap(), mapping.name());
    }

    @Override
    protected FieldMapping convertField(FieldMapping mapping, TypeMapping newParent) {
        return newParent.newField(mapping.remap(), mapping.name());
    }

    @Override
    protected MethodMapping convertMethod(MethodMapping mapping, TypeMapping newParent) {
        return newParent.newMethod(mapping.remap(), mapping.root().remapDescriptor(mapping.desc()), mapping.name());
    }

    @Override
    protected LvtMapping convertLvt(LvtMapping mapping, MethodMapping newParent) {
        return newParent.newLvt(mapping.index(), mapping.remap(), mapping.root().remapDescriptor(mapping.desc()), mapping.name());
    }
}
