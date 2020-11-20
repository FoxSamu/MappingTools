package net.shadew.asm.mappings.io;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.shadew.asm.mappings.model.Mappings;
import net.shadew.asm.mappings.remap.DirectoryClassExport;
import net.shadew.asm.mappings.remap.DirectoryClassSource;
import net.shadew.asm.mappings.remap.JavaRemapper;

public final class RemapTest {
    private RemapTest() {
    }

    public static void main(String[] args) throws Exception {
        InputStream res = ProguardIOTest.class.getClassLoader().getResourceAsStream("testfile.txt");
        assert res != null;
        Mappings mappings = ProguardOutputMappingsIO.read(new InputStreamReader(res));

        File in = new File("./build/classes/java/test");
        File out = new File("./outfiles");

        JavaRemapper.remap(mappings, new DirectoryClassSource(in), new DirectoryClassExport(out), 100);
    }
}
