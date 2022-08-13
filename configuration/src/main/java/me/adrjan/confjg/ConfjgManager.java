package me.adrjan.confjg;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.function.Consumer;

@AllArgsConstructor
public class ConfjgManager {

    private final Gson gson;
    public static final String PATH_PATTERN = "%s\\%s.yml";

    public <T> T registerConfjg(Class<T> type) throws Exception {
        if (!type.isAnnotationPresent(ConfjgInfo.class))
            throw new Exception("ConfjgInfo annotation not provided.");
        ConfjgInfo confjgInfo = type.getAnnotation(ConfjgInfo.class);

        File dir = new File(confjgInfo.path());
        File file = new File(PATH_PATTERN.formatted(confjgInfo.path(), confjgInfo.name()));

        if (!file.exists()) {
            dir.mkdirs();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            T createInstantion = type.newInstance();
            fileWriter.write(this.gson.toJson(createInstantion));
            fileWriter.flush();
            fileWriter.close();
        }

        T instance = this.gson.fromJson(new BufferedReader(new FileReader(file)), type);
        instance.getClass().getMethod("setGson", Gson.class).invoke(instance, this.gson);
        return instance;
    }

    public <T> T registerConfjg(Class<T> type, boolean updateOnStartup) throws Exception {
        T instance = this.registerConfjg(type);
        if (updateOnStartup) ((Confjg) instance).save();
        return instance;
    }

    public static class Builder {

        public final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        public <T> Builder addGsonAdapter(Type type, GsonSerializerAdapter<T> adapter) {
            gsonBuilder.registerTypeAdapter(type, adapter);
            return this;
        }

        public Builder extra(Consumer<GsonBuilder> consumer){
            consumer.accept(gsonBuilder);
            return this;
        }

        public ConfjgManager build() {
            return new ConfjgManager(gsonBuilder.create());
        }
    }
}