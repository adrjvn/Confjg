package me.adrjan.confjg;

import com.google.gson.Gson;
import lombok.Setter;
import lombok.SneakyThrows;
import me.adrjan.confjg.annotation.ConfjgInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public abstract class Confjg extends ConfjgWrapper {

    @Setter
    transient private Gson gson;
    transient private File file;
    final transient private ConfjgInfo confjgInfo = getClass().getAnnotation(ConfjgInfo.class);

    @SneakyThrows
    @Override
    protected void saveConfig() {
        try (FileWriter fileWriter = new FileWriter(this.file)){
            fileWriter.write(this.gson.toJson(this));
            fileWriter.flush();
        }
    }

    @SneakyThrows
    @Override
    protected Confjg instance() {
        return this.gson.fromJson(new BufferedReader(new FileReader(file)), getClass());
    }

    public void setDataFile(File file) {
        if (file == null) {
            this.file = new File(ConfjgManager.PATH_PATTERN.formatted(confjgInfo.path(), confjgInfo.name()));
            return;
        }
        this.file = new File(confjgInfo.path(), confjgInfo.name() + ConfjgManager.EXTENSION);
    }
}