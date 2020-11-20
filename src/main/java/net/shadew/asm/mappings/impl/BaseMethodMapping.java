package net.shadew.asm.mappings.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.stream.Stream;

import net.shadew.asm.mappings.model.LvtMapping;
import net.shadew.asm.mappings.model.MethodMapping;
import net.shadew.asm.mappings.model.TypeMapping;
import net.shadew.util.contract.Validate;
import net.shadew.util.data.Pair;

public class BaseMethodMapping extends BaseMapping implements MethodMapping {
    private final TypeMapping parent;
    private final String desc;
    private final Object2ObjectMap<Pair<Integer, String>, LvtMapping> lvtMappings = new Object2ObjectLinkedOpenHashMap<>();

    public BaseMethodMapping(TypeMapping parent, String name, String desc) {
        super(name);
        Validate.notNull(desc, "desc");
        this.desc = desc;
        Validate.notNull(parent, "parent");
        this.parent = parent;
    }

    public BaseMethodMapping(TypeMapping parent, String name, String desc, String newName) {
        super(name, newName);
        Validate.notNull(desc, "desc");
        this.desc = desc;
        Validate.notNull(parent, "parent");
        this.parent = parent;
    }

    @Override
    public TypeMapping parent() {
        return parent;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public LvtMapping lvt(int index, String desc) {
        Validate.notNull(desc, "desc");
        return lvtMappings.get(Pair.of(index, desc));
    }

    @Override
    public Stream<LvtMapping> lvts() {
        return lvtMappings.values().stream();
    }

    @Override
    public void addLvt(LvtMapping mapping) {
        Validate.notNull(mapping, "mapping");
        Validate.isTrue(mapping.parent() == this, "Mapping parent does not match");
        lvtMappings.put(Pair.of(mapping.index(), mapping.desc()), mapping);
    }

    @Override
    public void removeLvt(int index, String desc) {
        Validate.notNull(desc, "desc");
        lvtMappings.remove(Pair.of(index, desc));
    }

    @Override
    public boolean hasLvt(int index, String desc) {
        Validate.notNull(desc, "desc");
        return lvtMappings.containsKey(Pair.of(index, desc));
    }
}
