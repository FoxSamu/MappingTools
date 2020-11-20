package net.shadew.asm.mappings.remap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class JarClassSource implements ClassSource {
    private final JarFile jar;

    public JarClassSource(File file) throws IOException {
        this.jar = new JarFile(file);
    }

    @Override
    public ClassReference resolveClass(String internalName) {
        JarEntry entry = jar.getJarEntry(internalName + ".class");
        if (entry == null) return null;
        return new Ref(internalName, jar, entry);
    }

    @Override
    public Stream<ClassReference> allClasses() {
        return jar.stream()
                  .filter(entry -> entry.getName().endsWith(".class"))
                  .map(entry -> {
                      String name = entry.getName();
                      name = name.substring(name.length() - 6);
                      return new Ref(name, jar, entry);
                  });
    }

    @Override
    public void close() throws IOException {
        jar.close();
    }

    private static class Ref implements ClassReference {
        private final String name;
        private final JarFile jar;
        private final JarEntry entry;

        private Ref(String name, JarFile jar, JarEntry entry) {
            this.name = name;
            this.jar = jar;
            this.entry = entry;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public InputStream openStream() throws IOException {
            return jar.getInputStream(entry);
        }
    }
}
