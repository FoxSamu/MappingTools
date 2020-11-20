package net.shadew.asm.mappings.remap;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public interface ClassExport extends AutoCloseable, Closeable {
    OutputStream export(String className) throws IOException;
}
