package net.shadew.asm.mappings.model;

import java.util.stream.Stream;

import net.shadew.asm.descriptor.*;
import net.shadew.asm.mappings.impl.BaseMappings;
import net.shadew.asm.mappings.impl.BaseTypeMapping;
import net.shadew.asm.mappings.visit.MappingsConsumer;
import net.shadew.asm.mappings.visit.MappingsConverter;

public interface Mappings {
    TypeMapping type(String typeName);
    Stream<TypeMapping> types();
    void addType(TypeMapping mapping);
    void removeType(String typeName);
    boolean hasType(String typeName);

    default TypeMapping newType(String name, String def) {
        TypeMapping out = type(name);
        if (out == null) {
            out = new BaseTypeMapping(this, name, def);
            addType(out);
        }
        return out;
    }

    default TypeMapping newType(String name) {
        return newType(name, name);
    }

    default String remapDescriptor(String desc) {
        return remapDescriptor(Descriptor.parse(desc)).toString();
    }

    default Descriptor remapDescriptor(Descriptor desc) {
        if (desc.isMethod()) {
            MethodDescriptor mdesc = desc.asMethod();
            return MethodDescriptor.of(
                remapDescriptor(mdesc.returnType()).asType(),
                mdesc.parameters()
                     .map(this::remapDescriptor)
                     .map(Descriptor::asType)
                     .toArray(TypeDescriptor[]::new)
            );
        } else if (desc.isArray()) {
            ArrayDescriptor adesc = desc.asArray();
            return ArrayDescriptor.of(remapDescriptor(adesc.root()).asType(), adesc.dimensions());
        } else if (desc.isReference()) {
            ReferenceDescriptor rdesc = desc.asReference();
            TypeMapping mapping = type(rdesc.internalName());
            if (mapping == null) return rdesc;
            return ReferenceDescriptor.of(mapping.remap());
        } else {
            return desc;
        }
    }

    default void accept(MappingsConsumer consumer) {
        types().forEach(type -> {
            consumer.visitType(type);
            type.fields().forEach(consumer::visitField);
            type.methods().forEach(method -> {
                consumer.visitMethod(method);
                method.lvts().forEach(consumer::visitLvt);
            });
        });
    }

    default Mappings convert(MappingsConverter converter) {
        accept(converter);
        return converter.getMappings();
    }

    static Mappings create() {
        return new BaseMappings();
    }
}
