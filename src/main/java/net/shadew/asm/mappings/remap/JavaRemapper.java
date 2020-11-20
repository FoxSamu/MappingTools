package net.shadew.asm.mappings.remap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.OutputStream;
import java.util.stream.Stream;

import net.shadew.asm.mappings.model.Mappings;

public final class JavaRemapper {
    private JavaRemapper() {
    }

    public static void remap(Mappings mappings, ClassSource source, ClassExport export, int caching) throws Exception {
        try (Stream<ClassReference> classes = source.allClasses();
             AsmCache cache = new AsmCache(source, caching);
             ClassExport exp = export) {

            SuperclassCache supers = new SuperclassCache(cache);
            AsmRemapper remapper = new AsmRemapper(mappings, supers);

            classes.forEach(cls -> {
                try {
                    ClassNode node = cache.resolve(cls.name());
                    ClassWriter writer = new ClassWriter(0);
                    ClassRemapper cr = new ClassRemapper(writer, remapper);
                    node.accept(cr);
                    String outName = remapper.map(node.name);
                    try (OutputStream out = exp.export(outName)) {
                        out.write(writer.toByteArray());
                    }
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            });
        }
    }
}
