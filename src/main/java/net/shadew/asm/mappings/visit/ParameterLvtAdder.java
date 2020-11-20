package net.shadew.asm.mappings.visit;

import net.shadew.asm.descriptor.MethodDescriptor;
import net.shadew.asm.descriptor.TypeDescriptor;
import net.shadew.asm.mappings.model.FieldMapping;
import net.shadew.asm.mappings.model.LvtMapping;
import net.shadew.asm.mappings.model.MethodMapping;
import net.shadew.asm.mappings.model.TypeMapping;

public class ParameterLvtAdder implements MappingsConsumer {
    private void createLVT(MethodDescriptor desc, MethodMapping mapping) {
        int size = 0;
        int len = 0;
        for (TypeDescriptor arg : desc.parameterArray()) {
            mapping.newLvt(size, "par" + len, arg.toString(), "par" + len);
            size += arg.size();
            len++;
        }
    }

    @Override
    public void visitType(TypeMapping mapping) {

    }

    @Override
    public void visitField(FieldMapping mapping) {

    }

    @Override
    public void visitMethod(MethodMapping mapping) {
        createLVT(MethodDescriptor.parse(mapping.desc()), mapping);
    }

    @Override
    public void visitLvt(LvtMapping mapping) {

    }
}
