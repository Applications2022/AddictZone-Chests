package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.model.player.ChestHistoryEntry;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChestHistoryService {

    private AddictzoneChests plugin;

    public ChestHistoryService(AddictzoneChests plugin) {
        this.plugin = plugin;
    }

    public void addHistory(UUID uuid, String chest, Date date, ChestItem win){
        addHistory(uuid, new ChestHistoryEntry(UUID.randomUUID(), chest, date, win));
    }

    public void addHistory(UUID uuid, ChestHistoryEntry chestHistoryEntry){

        Document update = chestHistoryEntry.toDocument();

        getCollection().updateOne(Filters.eq("_id", uuid), Updates.addToSet("history", update));
//        Document document = getUser(uuid);
//
//        List<Document> histories = document.getList("history", Document.class);
//
//        histories.add(chestHistoryEntry.toDocument());
//
//        document.replace("history", histories);
//
//        getCollection().replaceOne(Filters.eq("_id", uuid), document);
    }

    public void removeHistory(UUID playerId, UUID uuid){
        Document document = getUser(playerId);

        List<Document> histories = document.getList("history", Document.class);

        histories.removeIf(document1 -> new ChestHistoryEntry().fromDocument(document1).getUuid().toString().equals(uuid.toString()));

        document.replace("history", histories);

        getCollection().replaceOne(Filters.eq("_id", playerId), document);
    }

    public List<ChestHistoryEntry> getHistory(UUID uuid){
        return getUser(uuid).getList("history", Document.class).stream().map(document -> new ChestHistoryEntry().fromDocument(document)).collect(Collectors.toList());
    }

    public Document getUser(UUID uuid){
        if(!existUser(uuid)) createUser(uuid);
        return getCollection().find(Filters.eq("_id", uuid)).first();
    }

    public void createUser(UUID uuid){
        getCollection().insertOne(new Document("_id", uuid).append("history", new ArrayList<>()));
    }

    public boolean existUser(UUID uuid){
        return getCollection().find(Filters.eq("_id", uuid)).first() != null;
    }

    private MongoCollection<Document> getCollection(){
        return plugin.getMongoDBStorage().getMongoDatabase().getCollection("Data_ChestHistories");
    }
}
