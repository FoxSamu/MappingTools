package net.shadew.asm.mappings.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.shadew.asm.mappings.model.Mappings;

public final class JoinTest {
    private JoinTest() {
    }

    public static void main(String[] args) {
        Mappings mappings = Mappings.create();
        mappings.newType("abc", "net/test/ABCClass")
                .newMethod("a", "()V", "aMethod").parent()
                .newMethod("b", "(Ldef;Ljava/lang/String;)Labc;", "bMethod").parent()
                .newMethod("c", "(I)V", "cMethod")
                .newLvt(0, "", "Lnet/test/ABCClass;", "this").parent()
                .newLvt(1, "", "I", "i").parent()
                .parent()
                .newField("x", "xField").parent()
                .newField("y", "yField").parent();
        mappings.newType("def", "net/test/DEFClass")
                .newField("a", "aField").parent()
                .newField("b", "bField").parent();

        Mappings mappings2 = Mappings.create();
        mappings2.newType("ghi", "net/test/GHIClass")
                 .newMethod("a", "()V", "aMethod").parent()
                 .newMethod("b", "(Ldef;Ljava/lang/String;)Labc;", "bMethod").parent()
                 .newMethod("c", "(I)V", "cMethod")
                 .newLvt(0, "this", "Lnet/test/GHIClass;", "this").parent()
                 .newLvt(1, "", "I", "i").parent()
                 .parent()
                 .newField("x", "xField").parent()
                 .newField("y", "yField").parent();
        mappings2.newType("def", "net/test/DEFClass")
                 .newField("a", "aField").parent()
                 .newField("b", "bField").parent();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(JsonMappingsIO.toJson(MappingsOperations.join(mappings, mappings2)), System.out);
    }
}
