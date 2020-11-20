package net.shadew.asm.mappings.remap;

import java.io.Closeable;
import java.net.URL;
import java.util.Iterator;
import java.util.stream.Stream;

public interface ClassSource extends AutoCloseable, Closeable {
    ClassReference resolveClass(String internalName) throws Exception;
    Stream<ClassReference> allClasses() throws Exception;
}
