package me.adrjan.confjg;

import com.google.gson.Gson;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class Confjg {

    @Setter
    transient private Gson gson;
    transient private File file;
    final transient private ConfjgInfo confjgInfo;

    @SneakyThrows
    public Confjg() {
        this.confjgInfo = getClass().getAnnotation(ConfjgInfo.class);
        this.file = new File(confjgInfo.path() + "\\" + confjgInfo.name() + ".yml");
    }

    @SneakyThrows
    public void save() {
        FileWriter fileWriter = new FileWriter(this.file);
        fileWriter.write(this.gson.toJson(this));
        fileWriter.flush();
        fileWriter.close();
    }

    @SneakyThrows
    public void reload() { //XDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
        this.file = new File(confjgInfo.path() + "\\" + confjgInfo.name() + ".yml");
        Field[] fields = getClass().getDeclaredFields();
        Confjg instance = this.gson.fromJson(new BufferedReader(new FileReader(file)), getClass());
        Field[] newFields = instance.getClass().getDeclaredFields();
        Map<String, Field> fieldsMap = new HashMap<>();
        for (Field newField : newFields) {
            newField.setAccessible(true);
            fieldsMap.put(newField.getName(), newField);
        }
        for (Field field : fields) {
            field.setAccessible(true);
            Field newField = fieldsMap.get(field.getName());
            field.set(this, newField.get(instance));
        }
    }
}