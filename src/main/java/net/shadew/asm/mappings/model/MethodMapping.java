package net.shadew.asm.mappings.model;

import java.util.stream.Stream;

import net.shadew.asm.mappings.impl.BaseLvtMapping;

public interface MethodMapping extends Mapping {
    TypeMapping parent();

    String desc();

    LvtMapping lvt(int index, String desc);
    Stream<LvtMapping> lvts();

    void addLvt(LvtMapping mapping);
    void removeLvt(int index, String desc);
    boolean hasLvt(int index, String desc);

    default Mappings root() {
        return parent().parent();
    }

    default LvtMapping newLvt(int index, String name, String desc, String def) {
        LvtMapping out = lvt(index, desc);
        if (out == null) {
            out = new BaseLvtMapping(this, index, name, desc, def);
            addLvt(out);
        }
        return out;
    }

    default LvtMapping newLvt(int index, String name, String desc) {
        return newLvt(index, name, desc, name);
    }
}
