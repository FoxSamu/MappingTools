package net.shadew.asm.mappings.model;

import java.util.stream.Stream;

import net.shadew.asm.mappings.impl.BaseFieldMapping;
import net.shadew.asm.mappings.impl.BaseMethodMapping;

public interface TypeMapping extends Mapping {
    Mappings parent();

    FieldMapping field(String name);
    MethodMapping method(String name, String desc);

    Stream<FieldMapping> fields();
    Stream<MethodMapping> methods();

    void addField(FieldMapping mapping);
    void removeField(String name);
    boolean hasField(String name);
    void addMethod(MethodMapping mapping);
    void removeMethod(String name, String desc);
    boolean hasMethod(String name, String desc);

    default Mappings root() {
        return parent();
    }

    default FieldMapping newField(String name, String def) {
        FieldMapping out = field(name);
        if (out == null) {
            out = new BaseFieldMapping(this, name, def);
            addField(out);
        }
        return out;
    }

    default FieldMapping newField(String name) {
        return newField(name, name);
    }

    default MethodMapping newMethod(String name, String desc, String def) {
        MethodMapping out = method(name, desc);
        if (out == null) {
            out = new BaseMethodMapping(this, name, desc, def);
            addMethod(out);
        }
        return out;
    }

    default MethodMapping newMethod(String name, String desc) {
        return newMethod(name, desc, name);
    }
}
