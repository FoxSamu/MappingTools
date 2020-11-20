package net.shadew.asm.mappings.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import net.shadew.asm.mappings.model.Mappings;

public final class RMapIOTest {
    private RMapIOTest() {
    }

    public static void main(String[] args) throws IOException {
        Mappings mappings = Mappings.create();
        mappings.newType("abc", "net/test/ABCClass")
                .newMethod("a", "()V", "aMethod").parent()
                .newMethod("b", "()V", "bMethod").parent()
                .newMethod("c", "(I)V", "cMethod")
                .newLvt(0, "", "Lnet/test/ABCClass;", "this").parent()
                .newLvt(1, "", "I", "i").parent()
                .parent()
                .newField("x", "xField").parent()
                .newField("y", "yField").parent();
        mappings.newType("def", "net/test/DEFClass")
                .newField("a", "aField").parent()
                .newField("b", "bField").parent();

        StringWriter out = new StringWriter();
        RMapMappingsIO.write(out, mappings);
        Mappings in = RMapMappingsIO.read(new StringReader(out.toString()));
        RMapMappingsIO.write(System.out, in);
    }
}
