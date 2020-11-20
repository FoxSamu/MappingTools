package net.shadew.asm.mappings.remap;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarClassExport implements ClassExport {
    private final JarOutputStream out;
    private final WrappingStream wrapper = new WrappingStream();

    public JarClassExport(File jar) throws IOException {
        jar.getParentFile().mkdirs();
        this.out = new JarOutputStream(new FileOutputStream(jar));
    }

    @Override
    public OutputStream export(String className) throws IOException {
        out.putNextEntry(new JarEntry(className + ".class"));
        return wrapper;
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    private class WrappingStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            out.closeEntry();
        }
    }
}
