package net.shadew.asm.mappings.impl;

import net.shadew.asm.mappings.model.Mapping;
import net.shadew.util.contract.Validate;

public abstract class BaseMapping implements Mapping {
    private final String name;
    private String newName;

    public BaseMapping(String name) {
        Validate.notNull(name, "name");
        this.name = name;
    }

    public BaseMapping(String name, String newName) {
        this(name);
        this.newName = newName;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String get() {
        return newName;
    }

    @Override
    public void set(String to) {
        newName = to;
    }

    @Override
    public void clear() {
        newName = null;
    }
}
