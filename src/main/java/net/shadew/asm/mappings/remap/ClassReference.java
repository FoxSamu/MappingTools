package net.shadew.asm.mappings.remap;

import java.io.IOException;
import java.io.InputStream;

public interface ClassReference {
    String name();
    InputStream openStream() throws IOException;
}
