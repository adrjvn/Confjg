package me.adrjan.confjg;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Setter;
import lombok.SneakyThrows;
import me.adrjan.confjg.annotation.ConfjgInfo;
import org.bson.Document;

public abstract class MongoConfjg extends ConfjgWrapper {

    @Setter
    transient private Gson gson;
    @Setter
    transient private MongoCollection<Document> collection;

    final transient private ConfjgInfo confjgInfo;

    @SneakyThrows
    public MongoConfjg() {
        this.confjgInfo = getClass().getAnnotation(ConfjgInfo.class);
    }

    @SneakyThrows
    @Override
    protected void saveConfig() {
        Document document = Document.parse(this.gson.toJson(this));
        collection.replaceOne(Filters.eq("_id", this.confjgInfo.name()), document, new ReplaceOptions().upsert(true));
    }

    @Override
    protected MongoConfjg instance() {
        return this.gson.fromJson(getDocument().toJson(), getClass());
    }

    private Document getDocument() {
        return collection.find(Filters.eq("_id", this.confjgInfo.name())).first();
    }

    private void initialize() {
        Document document = getDocument();
        if (document == null) {
            document = Document.parse(this.gson.toJson(this));
            this.saveConfig();
        }
        super.reload();
    }
}