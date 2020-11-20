package net.shadew.asm.mappings.remap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZippedClassSource implements ClassSource {
    private final ZipFile zip;

    public ZippedClassSource(File file) throws IOException {
        this.zip = new ZipFile(file);
    }

    @Override
    public ClassReference resolveClass(String internalName) {
        ZipEntry entry = zip.getEntry(internalName + ".class");
        if (entry == null) return null;
        return new Ref(internalName, zip, entry);
    }

    @Override
    public Stream<ClassReference> allClasses() {
        return zip.stream()
                  .filter(entry -> entry.getName().endsWith(".class"))
                  .map(entry -> {
                      String name = entry.getName();
                      name = name.substring(name.length() - 6);
                      return new Ref(name, zip, entry);
                  });
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }

    private static class Ref implements ClassReference {
        private final String name;
        private final ZipFile zip;
        private final ZipEntry entry;

        private Ref(String name, ZipFile zip, ZipEntry entry) {
            this.name = name;
            this.zip = zip;
            this.entry = entry;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public InputStream openStream() throws IOException {
            return zip.getInputStream(entry);
        }
    }
}
