package net.shadew.asm.mappings.remap;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class AsmCache implements AutoCloseable, Closeable {
    private final ClassSource src;
    private final Object2ObjectLinkedOpenHashMap<String, ClassNode> cached = new Object2ObjectLinkedOpenHashMap<>();
    private final Set<String> unknown = new ObjectOpenHashSet<>();
    private final int maxCache;

    public AsmCache(ClassSource src, int maxCache) {
        this.src = src;
        this.maxCache = maxCache;
    }

    private ClassNode load(String name) throws Exception {
        ClassReference ref = src.resolveClass(name);
        if (ref == null) return null;
        ClassNode node = new ClassNode();
        try (InputStream in = ref.openStream()) {
            ClassReader reader = new ClassReader(in);
            reader.accept(node, ClassReader.EXPAND_FRAMES);
        }
        return node;
    }

    public ClassNode resolve(String name) throws Exception {
        if (unknown.contains(name)) {
            return null;
        }
        if (cached.containsKey(name)) {
            return cached.getAndMoveToFirst(name);
        }

        ClassNode loaded = load(name);
        if (loaded == null) {
            unknown.add(name);
        } else {
            cached.putAndMoveToFirst(name, loaded);
            while (cached.size() > maxCache) {
                cached.removeLast();
            }
        }

        return loaded;
    }

    public ClassSource getSource() {
        return src;
    }

    @Override
    public void close() throws IOException {
        src.close();
    }
}
