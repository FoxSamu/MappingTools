package net.shadew.asm.mappings.io;

import net.shadew.asm.mappings.model.*;

public final class MappingsOperations {
    private MappingsOperations() {
    }

    public static Mappings join(Mappings a, Mappings b) {
        Mappings out = Mappings.create();
        a.types().forEach(type -> {
            TypeMapping typeOut = out.newType(
                type.name(),
                type.remap()
            );
            type.fields().forEach(field -> typeOut.newField(
                field.name(),
                field.remap()
            ));
            type.methods().forEach(method -> {
                MethodMapping methodOut = typeOut.newMethod(
                    method.name(),
                    method.desc(),
                    method.remap()
                );
                method.lvts().forEach(lvt -> methodOut.newLvt(
                    lvt.index(),
                    lvt.name(),
                    lvt.desc(),
                    lvt.remap()
                ));
            });
        });
        b.types().forEach(type -> {
            TypeMapping existing = out.type(type.name());
            if (existing != null && !existing.remap().equals(type.remap()))
                throw new IllegalStateException("Mappings do not overlap on type " + type.name() + ", has both " + type.remap() + " and " + existing.remap());
            TypeMapping typeOut = out.newType(
                type.name(),
                type.remap()
            );
            type.fields().forEach(field -> {
                FieldMapping existingField = existing != null ? existing.field(field.name()) : null;
                if (existingField != null && !existingField.remap().equals(field.remap()))
                    throw new IllegalStateException("Mappings do not overlap on field " + type.name() + "." + field.name() + ", has both " + field.remap() + " and " + existingField.remap());
                typeOut.newField(
                    field.name(),
                    field.remap()
                );
            });
            type.methods().forEach(method -> {
                MethodMapping existingMethod = existing != null ? existing.method(method.name(), method.desc()) : null;
                if (existingMethod != null && !existingMethod.remap().equals(method.remap()))
                    throw new IllegalStateException("Mappings do not overlap on method " + type.name() + "." + method.name() + method.desc() + ", has both " + method.remap() + " and " + existingMethod.remap());
                MethodMapping methodOut = typeOut.newMethod(
                    method.name(),
                    method.desc(),
                    method.remap()
                );
                method.lvts().forEach(lvt -> {
                    LvtMapping existingLvt = existingMethod != null ? existingMethod.lvt(lvt.index(), lvt.desc()) : null;
                    if (existingLvt != null && !existingLvt.remap().equals(lvt.remap()))
                        throw new IllegalStateException("Mappings do not overlap on LVT " + type.name() + "." + method.name() + method.desc() + "#" + lvt.index() + ":" + lvt.desc() + ", has both " + lvt.remap() + " and " + existingLvt.remap());

                    methodOut.newLvt(
                        lvt.index(),
                        lvt.name(),
                        lvt.desc(),
                        lvt.remap()
                    );
                });
            });
        });
        return out;
    }

    public static Mappings reverse(Mappings mappings) {
        Mappings out = Mappings.create();
        mappings.types().forEach(type -> {
            TypeMapping typeOut = out.newType(
                type.remap(),
                type.name()
            );
            type.fields().forEach(field -> typeOut.newField(
                field.remap(),
                field.name()
            ));
            type.methods().forEach(method -> {
                MethodMapping methodOut = typeOut.newMethod(
                    method.remap(),
                    mappings.remapDescriptor(method.desc()),
                    method.name()
                );
                method.lvts().forEach(lvt -> methodOut.newLvt(
                    lvt.index(),
                    lvt.remap(),
                    mappings.remapDescriptor(lvt.desc()),
                    lvt.name()
                ));
            });
        });
        return out;
    }
}
