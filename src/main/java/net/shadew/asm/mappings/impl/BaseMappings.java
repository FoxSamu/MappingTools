package net.shadew.asm.mappings.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.stream.Stream;

import net.shadew.asm.mappings.model.Mappings;
import net.shadew.asm.mappings.model.TypeMapping;
import net.shadew.util.contract.Validate;

public class BaseMappings implements Mappings {
    private final Object2ObjectMap<String, TypeMapping> types = new Object2ObjectLinkedOpenHashMap<>();

    @Override
    public TypeMapping type(String typeName) {
        Validate.notNull(typeName, "typeName");
        return types.get(typeName);
    }

    @Override
    public Stream<TypeMapping> types() {
        return types.values().stream();
    }

    @Override
    public void addType(TypeMapping mapping) {
        Validate.notNull(mapping, "mapping");
        Validate.isTrue(mapping.parent() == this, "Mapping parent does not match");
        types.put(mapping.name(), mapping);
    }

    @Override
    public void removeType(String typeName) {
        Validate.notNull(typeName, "typeName");
        types.remove(typeName);
    }

    @Override
    public boolean hasType(String typeName) {
        Validate.notNull(typeName, "typeName");
        return types.containsKey(typeName);
    }
}
