package net.shadew.asm.mappings.remap;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZippedClassExport implements ClassExport {
    private final ZipOutputStream out;
    private final WrappingStream wrapper = new WrappingStream();

    public ZippedClassExport(File zip) throws FileNotFoundException {
        zip.getParentFile().mkdirs();
        this.out = new ZipOutputStream(new FileOutputStream(zip));
    }

    @Override
    public OutputStream export(String className) throws IOException {
        out.putNextEntry(new ZipEntry(className + ".class"));
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
