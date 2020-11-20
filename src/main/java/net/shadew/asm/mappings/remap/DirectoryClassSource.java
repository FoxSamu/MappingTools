package net.shadew.asm.mappings.remap;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DirectoryClassSource implements ClassSource {
    private final File directory;

    public DirectoryClassSource(File directory) {
        this.directory = directory;
    }

    @Override
    public ClassReference resolveClass(String internalName) {
        File path = new File(directory.getAbsoluteFile(), internalName + ".class");
        if (!path.exists()) return null;
        return new Ref(internalName, path);
    }

    @Override
    public Stream<ClassReference> allClasses() throws Exception {
        Path path = directory.getAbsoluteFile().toPath().normalize();
        String root = path.toString();
        int rl = root.length();

        return Files.walk(path).filter(
            file -> file.toString().endsWith(".class")
        ).map(
            file -> {
                String abs = file.toString();
                assert abs.startsWith(root);
                return new Ref(abs.substring(rl, abs.length() - 6), file.toFile());
            }
        );
    }

    @Override
    public void close() {
    }

    private static class Ref implements ClassReference {
        private final String name;
        private final File file;

        private Ref(String name, File file) {
            this.name = name;
            this.file = file;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public InputStream openStream() throws FileNotFoundException {
            return new FileInputStream(file);
        }
    }
}
