package net.shadew.asm.mappings.remap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DirectoryClassExport implements ClassExport {
    private final File directory;

    public DirectoryClassExport(File directory) {
        this.directory = directory;
    }

    @Override
    public OutputStream export(String className) throws IOException {
        File path = new File(directory, className + ".class");
        path.getParentFile().mkdirs();
        return new FileOutputStream(path);
    }

    @Override
    public void close() {
    }
}
