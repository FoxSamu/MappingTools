package net.shadew.asm.mappings.io;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.shadew.asm.mappings.model.Mappings;

public final class ProguardIOTest {
    private ProguardIOTest() {
    }

    public static void main(String[] args) throws Exception {
        InputStream res = ProguardIOTest.class.getClassLoader().getResourceAsStream("testfile.txt");
        assert res != null;
        Mappings mappings = ProguardOutputMappingsIO.read(new InputStreamReader(res));

        RMapMappingsIO.write(System.out, mappings);
    }
}
