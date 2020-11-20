package net.shadew.asm.mappings.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.stream.Stream;

import net.shadew.asm.mappings.model.FieldMapping;
import net.shadew.asm.mappings.model.Mappings;
import net.shadew.asm.mappings.model.MethodMapping;
import net.shadew.asm.mappings.model.TypeMapping;
import net.shadew.util.contract.Validate;
import net.shadew.util.data.Pair;

public class BaseTypeMapping extends BaseMapping implements TypeMapping {
    private final Mappings parent;
    private final Object2ObjectMap<String, FieldMapping> fields = new Object2ObjectLinkedOpenHashMap<>();
    private final Object2ObjectMap<Pair<String, String>, MethodMapping> methods = new Object2ObjectLinkedOpenHashMap<>();

    public BaseTypeMapping(Mappings parent, String name) {
        super(name);
        Validate.notNull(parent, "parent");
        this.parent = parent;
    }

    public BaseTypeMapping(Mappings parent, String name, String newName) {
        super(name, newName);
        Validate.notNull(parent, "parent");
        this.parent = parent;
    }

    @Override
    public Mappings parent() {
        return parent;
    }

    @Override
    public FieldMapping field(String name) {
        Validate.notNull(name, "name");
        return fields.get(name);
    }

    @Override
    public MethodMapping method(String name, String desc) {
        Validate.notNull(name, "name");
        Validate.notNull(desc, "desc");
        return methods.get(Pair.of(name, desc));
    }

    @Override
    public Stream<FieldMapping> fields() {
        return fields.values().stream();
    }

    @Override
    public Stream<MethodMapping> methods() {
        return methods.values().stream();
    }

    @Override
    public void addField(FieldMapping mapping) {
        Validate.notNull(mapping, "mapping");
        Validate.isTrue(mapping.parent() == this, "Mapping parent does not match");
        fields.put(mapping.name(), mapping);
    }

    @Override
    public void removeField(String name) {
        Validate.notNull(name, "name");
        fields.remove(name);
    }

    @Override
    public boolean hasField(String name) {
        Validate.notNull(name, "name");
        return fields.containsKey(name);
    }

    @Override
    public void addMethod(MethodMapping mapping) {
        Validate.notNull(mapping, "mapping");
        Validate.isTrue(mapping.parent() == this, "Mapping parent does not match");
        methods.put(Pair.of(mapping.name(), mapping.desc()), mapping);
    }

    @Override
    public void removeMethod(String name, String desc) {
        Validate.notNull(name, "name");
        Validate.notNull(desc, "desc");
        methods.remove(Pair.of(name, desc));
    }

    @Override
    public boolean hasMethod(String name, String desc) {
        Validate.notNull(name, "name");
        Validate.notNull(desc, "desc");
        return methods.containsKey(Pair.of(name, desc));
    }
}
