package net.shadew.asm.mappings.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.shadew.asm.mappings.model.*;

public final class JsonMappingsIO {
    private JsonMappingsIO() {
    }

    private static JsonObject toJson(LvtMapping mapping) {
        JsonObject json = new JsonObject();
        json.addProperty("name", mapping.name());
        json.addProperty("desc", mapping.desc());
        json.addProperty("index", mapping.index());
        if (mapping.get() != null)
            json.addProperty("mapped", mapping.get());
        return json;
    }

    private static JsonObject toJson(MethodMapping mapping) {
        JsonObject json = new JsonObject();
        json.addProperty("name", mapping.name());
        json.addProperty("desc", mapping.desc());
        if (mapping.get() != null)
            json.addProperty("mapped", mapping.get());

        JsonArray lvts = new JsonArray();
        mapping.lvts().forEach(lvt -> lvts.add(toJson(lvt)));
        if (lvts.size() > 0) json.add("lvt", lvts);

        return json;
    }

    private static JsonObject toJson(FieldMapping mapping) {
        JsonObject json = new JsonObject();
        json.addProperty("name", mapping.name());
        if (mapping.get() != null)
            json.addProperty("mapped", mapping.get());
        return json;
    }

    private static JsonObject toJson(TypeMapping mapping) {
        JsonObject json = new JsonObject();
        json.addProperty("name", mapping.name());
        if (mapping.get() != null)
            json.addProperty("mapped", mapping.get());

        JsonArray fields = new JsonArray();
        mapping.fields().forEach(field -> fields.add(toJson(field)));
        if (fields.size() > 0) json.add("fields", fields);

        JsonArray methods = new JsonArray();
        mapping.methods().forEach(method -> methods.add(toJson(method)));
        if (methods.size() > 0) json.add("methods", methods);

        return json;
    }

    public static JsonArray toJson(Mappings mappings) {
        JsonArray json = new JsonArray();
        mappings.types().forEach(type -> json.add(toJson(type)));
        return json;
    }

    private static void fieldFromJson(JsonObject object, TypeMapping parent) {
        String name = object.get("name").getAsString();
        String mapped = object.has("mapped") ? object.get("mapped").getAsString() : null;
        parent.newField(name, mapped);
    }

    private static void lvtFromJson(JsonObject object, MethodMapping parent) {
        String name = object.get("name").getAsString();
        String desc = object.get("desc").getAsString();
        int index = object.get("index").getAsInt();
        String mapped = object.has("mapped") ? object.get("mapped").getAsString() : null;
        parent.newLvt(index, name, desc, mapped);
    }

    private static void methodFromJson(JsonObject object, TypeMapping parent) {
        String name = object.get("name").getAsString();
        String desc = object.get("desc").getAsString();
        String mapped = object.has("mapped") ? object.get("mapped").getAsString() : null;
        MethodMapping mapping = parent.newMethod(name, desc, mapped);
        if (object.has("lvt")) {
            JsonArray lvts = object.getAsJsonArray("lvt");
            for (JsonElement lvt : lvts) {
                lvtFromJson(lvt.getAsJsonObject(), mapping);
            }
        }
    }

    private static void typeFromJson(JsonObject object, Mappings parent) {
        String name = object.get("name").getAsString();
        String mapped = object.has("mapped") ? object.get("mapped").getAsString() : null;
        TypeMapping mapping = parent.newType(name, mapped);

        if (object.has("fields")) {
            JsonArray fields = object.getAsJsonArray("fields");
            for (JsonElement field : fields) {
                fieldFromJson(field.getAsJsonObject(), mapping);
            }
        }

        if (object.has("methods")) {
            JsonArray methods = object.getAsJsonArray("methods");
            for (JsonElement method : methods) {
                methodFromJson(method.getAsJsonObject(), mapping);
            }
        }
    }

    public static Mappings fromJson(JsonElement json) {
        Mappings mappings = Mappings.create();
        JsonArray types = json.getAsJsonArray();
        for (JsonElement type : types) {
            typeFromJson(type.getAsJsonObject(), mappings);
        }
        return mappings;
    }
}
