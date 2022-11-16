package me.adrjan.confjg;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfjgWrapper implements IConfjg {

    @Override
    public void save() {
        this.saveConfig();
        //announce
    }

    @SneakyThrows
    @Override
    public void reload() {
        IConfjg instance = this.instance();
        Field[] newFields = instance.getClass().getDeclaredFields();
        Field[] fields = getClass().getDeclaredFields();
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

    protected abstract void saveConfig();

    protected abstract IConfjg instance();
}