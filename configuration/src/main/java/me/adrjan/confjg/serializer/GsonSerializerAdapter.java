package me.adrjan.confjg.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;

public abstract class GsonSerializerAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public abstract T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext);

    @Override
    public abstract JsonElement serialize(T value, Type type, JsonSerializationContext jsonSerializationContext) ;
}