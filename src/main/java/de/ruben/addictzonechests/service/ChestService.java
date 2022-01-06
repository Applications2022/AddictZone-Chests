package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.xdevapi.XDevApi;
import org.bson.Document;
import org.cache2k.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ChestService(AddictzoneChests plugin) {

    public void createChest(String name, String prefix) {
        Chest chest = new Chest(UUID.randomUUID(), name, prefix, new ArrayList<>());

        if (!existChest(name)) {
            getCache().putIfAbsent(name, chest);

            XDevApi.getInstance().getxScheduler().async(() -> {
                getCollection().insertOne(chest.toDocument());
            });
        }
    }

    public void deleteChest(String name) {
        if (existChest(name)) {
            getCache().remove(name);

            getCollection().deleteOne(Filters.eq("name", name));
        }
    }

    public void addChestItem(String name, ChestItem chestItem) {
        if (existChest(name)) {

            Chest chest = getChest(name);

            List<ChestItem> items = chest.getItems();

            items.add(chestItem);

            chest.setItems(items);

            getCache().replace(name, chest);

            XDevApi.getInstance().getxScheduler().async(() -> {
                getCollection().replaceOne(Filters.eq("name", name), chest.toDocument());
            });

        }
    }

    public void removeChestItem(String name, ChestItem chestItem) {
        if (existChest(name)) {

            Chest chest = getChest(name);

            List<ChestItem> items = chest.getItems();

            items.removeIf(chestItem1 -> chestItem1.getId().toString().equals(chestItem.getId().toString()));

            chest.setItems(items);

            getCache().replace(name, chest);

            XDevApi.getInstance().getxScheduler().async(() -> {
                getCollection().replaceOne(Filters.eq("name", name), chest.toDocument());
            });

        }
    }

    public void removeChestItem(String name, UUID id) {
        if (existChest(name)) {

            Chest chest = getChest(name);

            List<ChestItem> items = chest.getItems();

            items.removeIf(chestItem1 -> chestItem1.getId().toString().equals(id.toString()));

            chest.setItems(items);

            getCache().replace(name, chest);

            XDevApi.getInstance().getxScheduler().async(() -> {
                getCollection().replaceOne(Filters.eq("name", name), chest.toDocument());
            });

        }
    }

    public List<Chest> getChests() {
        return getCache().asMap().values().stream().toList();
    }

    public Chest getChest(String name) {
        if (existChest(name)) {
//            return new Chest().fromDocument(getCollection().find(Filters.eq("name", name)).first());
            return getCache().get(name);
        }
        return null;
    }

    public boolean existChest(String name) {
        return getCollection().find(Filters.eq("name", name)).first() != null;
    }

    public void loadChestsIntoCache() {
        getCollection().find().into(new ArrayList<>()).forEach(document -> {
            Chest chest = new Chest().fromDocument(document);
            getCache().put(chest.getName(), chest);
        });
    }

    public MongoCollection<Document> getCollection() {
        return plugin.getMongoDBStorage().getMongoClient().getDatabase("Chests").getCollection("Data_Chest");
    }

    public Cache<String, Chest> getCache() {
        return plugin.getChestCache();
    }
}
