package net.shadew.asm.mappings.remap;

import org.objectweb.asm.commons.Remapper;

import java.util.List;

import net.shadew.asm.mappings.model.FieldMapping;
import net.shadew.asm.mappings.model.Mappings;
import net.shadew.asm.mappings.model.MethodMapping;
import net.shadew.asm.mappings.model.TypeMapping;

public class AsmRemapper extends Remapper {
    private final Mappings mappings;
    private final SuperclassCache supers;

    public AsmRemapper(Mappings mappings, SuperclassCache supers) {
        this.mappings = mappings;
        this.supers = supers;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        List<String> superclasses = supers.getSuperclasses(owner);
        for (String sup : superclasses) {
            TypeMapping type = mappings.type(sup);
            if (type == null) continue;

            MethodMapping met = type.method(name, descriptor);
            if (met != null) return met.remap();
        }

        return name;
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        List<String> superclasses = supers.getSuperclasses(owner);
        for (String sup : superclasses) {
            TypeMapping type = mappings.type(sup);
            if (type == null) continue;

            FieldMapping met = type.field(name);
            if (met != null) return met.remap();
        }

        return name;
    }

    @Override
    public String map(String internalName) {
        TypeMapping type = mappings.type(internalName);
        return type == null ? internalName : type.remap();
    }
}
