package me.adrjan.confjg.example.object;

import com.google.gson.*;
import me.adrjan.confjg.serializer.GsonSerializationAdapter;

import java.lang.reflect.Type;

public class FancyObjectAdapter extends GsonSerializationAdapter<FancyObject> {

    @Override
    public FancyObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new FancyObject(
                jsonObject.get("name").getAsString(),
                jsonObject.get("age").getAsInt()
        );
    }

    @Override
    public JsonElement serialize(FancyObject value, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("name", value.getName());
        jsonElement.addProperty("age", value.getAge());
        return jsonElement;
    }
}