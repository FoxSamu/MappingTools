package net.shadew.asm.mappings.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataOutput;

import net.shadew.asm.mappings.model.Mappings;

public final class JsonIOTest {
    private JsonIOTest() {
    }

    public static void main(String[] args) {
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

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(JsonMappingsIO.toJson(JsonMappingsIO.fromJson(JsonMappingsIO.toJson(mappings))), System.out);
    }
}
