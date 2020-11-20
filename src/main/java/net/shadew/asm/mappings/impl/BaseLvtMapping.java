package net.shadew.asm.mappings.impl;

import net.shadew.asm.mappings.model.LvtMapping;
import net.shadew.asm.mappings.model.MethodMapping;
import net.shadew.util.contract.Validate;

public class BaseLvtMapping extends BaseMapping implements LvtMapping {
    private final MethodMapping parent;
    private final int index;
    private final String desc;

    public BaseLvtMapping(MethodMapping parent, int index, String name, String desc) {
        super(name);
        Validate.notNull(parent, "parent");
        Validate.notNull(desc, "desc");
        this.parent = parent;
        this.index = index;
        this.desc = desc;
    }

    public BaseLvtMapping(MethodMapping parent, int index, String name, String desc, String newName) {
        super(name, newName);
        Validate.notNull(parent, "parent");
        Validate.notNull(desc, "desc");
        this.parent = parent;
        this.index = index;
        this.desc = desc;
    }

    @Override
    public MethodMapping parent() {
        return parent;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public String desc() {
        return desc;
    }
}
