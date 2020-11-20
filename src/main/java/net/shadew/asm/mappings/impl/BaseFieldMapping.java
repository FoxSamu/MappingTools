package net.shadew.asm.mappings.impl;

import net.shadew.asm.mappings.model.FieldMapping;
import net.shadew.asm.mappings.model.TypeMapping;
import net.shadew.util.contract.Validate;

public class BaseFieldMapping extends BaseMapping implements FieldMapping {
    private final TypeMapping parent;

    public BaseFieldMapping(TypeMapping parent, String name) {
        super(name);
        Validate.notNull(parent, "parent");
        this.parent = parent;
    }

    public BaseFieldMapping(TypeMapping parent, String name, String newName) {
        super(name, newName);
        Validate.notNull(parent, "parent");
        this.parent = parent;
    }

    @Override
    public TypeMapping parent() {
        return parent;
    }
}
