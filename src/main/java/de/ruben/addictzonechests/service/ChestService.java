package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestItem;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChestService {

    private AddictzoneChests plugin;

    public ChestService(AddictzoneChests plugin) {
        this.plugin = plugin;
    }

    public void createChest(String name){
        Chest chest = new Chest(UUID.randomUUID(), name, new ArrayList<>());

        if(!existChest(name)){
            getCollection().insertOne(chest.toDocument());
        }
    }

    public void addChestItem(String name, ChestItem chestItem){
        if(existChest(name)){

            Chest chest = getChest(name);

            List<ChestItem> items = chest.getItems();

            items.add(chestItem);

            chest.setItems(items);

            getCollection().replaceOne(Filters.eq("name", name), chest.toDocument());

        }
    }

    public void removeChestItem(String name, ChestItem chestItem){
        if(existChest(name)){

            Chest chest = getChest(name);

            List<ChestItem> items = chest.getItems();

            items.removeIf(chestItem1 -> chestItem1.getId().toString().equals(chestItem.getId().toString()));

            chest.setItems(items);

            getCollection().replaceOne(Filters.eq("name", name), chest.toDocument());

        }
    }

    public List<Chest> getChests(){
        return getCollection().find().into(new ArrayList<>()).stream().map(document -> new Chest().fromDocument(document)).collect(Collectors.toList());
    }

    public Chest getChest(String name){
        if(existChest(name)){
            return new Chest().fromDocument(getCollection().find(Filters.eq("name", name)).first());
        }
        return null;
    }

    public boolean existChest(String name){
        return getCollection().find(Filters.eq("name", name)).first() != null;
    }

    public MongoCollection<Document> getCollection(){
        return plugin.getMongoDBStorage().getMongoDatabase().getCollection("Data_Chest");
    }
}
