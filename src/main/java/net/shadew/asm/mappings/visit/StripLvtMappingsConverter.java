package net.shadew.asm.mappings.visit;

import net.shadew.asm.mappings.model.LvtMapping;
import net.shadew.asm.mappings.model.Mappings;
import net.shadew.asm.mappings.model.MethodMapping;

public class StripLvtMappingsConverter extends BaseMappingsConverter {
    public StripLvtMappingsConverter() {
    }

    public StripLvtMappingsConverter(Mappings mappings) {
        super(mappings);
    }

    @Override
    protected LvtMapping convertLvt(LvtMapping mapping, MethodMapping newParent) {
        return null;
    }
}
