package net.shadew.asm.mappings.remap;

import org.objectweb.asm.tree.ClassNode;

import java.util.*;

public class SuperclassCache {
    private final Map<String, List<String>> superclasses = new HashMap<>();
    private final AsmCache asmCache;

    public SuperclassCache(AsmCache asmCache) {
        this.asmCache = asmCache;
    }

    public List<String> getSuperclasses(String type) {
        if (superclasses.containsKey(type)) {
            return superclasses.get(type);
        } else {
            List<String> supers = new ArrayList<>();
            supers.add(type);
            ClassNode node;
            try {
                node = asmCache.resolve(type);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (node != null) {
                supers.add(node.superName);
                supers.addAll(getSuperclasses(node.superName));

                for (String ifc : node.interfaces) {
                    supers.add(ifc);
                    supers.addAll(getSuperclasses(ifc));
                }
            }
            supers = Collections.unmodifiableList(supers);
            superclasses.put(type, supers);
            return supers;
        }
    }
}
