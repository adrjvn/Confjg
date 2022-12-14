package me.adrjan.confjg;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.AllArgsConstructor;
import me.adrjan.confjg.annotation.ConfjgInfo;
import me.adrjan.confjg.serializer.GsonSerializerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.function.Consumer;

@AllArgsConstructor
public class ConfjgManager {

    private final Gson gson;
    private final MongoClient mongoClient;
    public static final String PATH_PATTERN = "%s\\%s.json";

    public <T extends IConfjg> T registerConfjg(Class<T> type) throws Exception {
        if (!type.isAnnotationPresent(ConfjgInfo.class))
            throw new Exception("ConfjgInfo annotation not provided.");
        ConfjgInfo confjgInfo = type.getAnnotation(ConfjgInfo.class);

        T instance = null;

        if (Confjg.class.isAssignableFrom(type)) {
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

            instance = this.gson.fromJson(new BufferedReader(new FileReader(file)), type);
            instance.getClass().getMethod("setGson", Gson.class).invoke(instance, this.gson);
        } else if (MongoConfjg.class.isAssignableFrom(type) && this.mongoClient != null) {
            instance = type.newInstance();
            instance.getClass().getMethod("setGson", Gson.class).invoke(instance, this.gson);
            instance.getClass().getMethod("setCollection", MongoCollection.class).invoke(instance, this.mongoClient.getDatabase(confjgInfo.database()).getCollection(confjgInfo.collection()));
            instance.getClass().getDeclaredMethod("initialize").setAccessible(true);
            instance.getClass().getDeclaredMethod("initialize").invoke(null);
        }
        return instance;
    }

    public <T extends IConfjg> T registerConfjg(Class<T> type, boolean updateOnStartup) throws Exception {
        T instance = this.registerConfjg(type);
        if (updateOnStartup) instance.save();
        return instance;
    }

    public static class Builder {

        public final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        public MongoClient mongoClient = null;

        public <T> Builder addGsonAdapter(Type type, GsonSerializerAdapter<T> adapter) {
            gsonBuilder.registerTypeAdapter(type, adapter);
            return this;
        }

        public Builder registerMongoClient(MongoClient mongoClient) {
            this.mongoClient = mongoClient;
            return this;
        }

        public Builder extra(Consumer<GsonBuilder> consumer) {
            consumer.accept(gsonBuilder);
            return this;
        }

        public ConfjgManager build() {
            return new ConfjgManager(gsonBuilder.create(), this.mongoClient);
        }
    }
}