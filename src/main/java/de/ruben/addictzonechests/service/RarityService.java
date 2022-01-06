package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.ItemRarity;
import de.ruben.xdevapi.XDevApi;
import org.bson.Document;
import org.cache2k.Cache;

import java.util.ArrayList;
import java.util.Collection;

public record RarityService(AddictzoneChests plugin) {

    public void createItemRarity(String name, String prefix, String message, Integer weight, Boolean broadcast) {

        if (existItemRarity(name)) {
            getItemRarity(name);
            return;
        }

        ItemRarity itemRarity = new ItemRarity(name, prefix, message, weight, broadcast);

        getCollection().insertOne(itemRarity.toDocument());
        getCache().putIfAbsent(name, itemRarity);

    }

    public void deleteItemRarity(String name) {
        if (existItemRarity(name)) {
            getCache().remove(name);
            getCollection().deleteOne(getItemRarity(name).toDocument());
        }
    }

    public Collection<ItemRarity> getItemRarities() {
        return getCache().asMap().values();
    }

    public void deleteItemRarityAndFormatItems(String name) {
        if (existItemRarity(name)) {

            XDevApi.getInstance().getxScheduler().async(() -> {
                ItemRarity itemRarity = getItemRarity(name);

                new ChestService(plugin).getChests().forEach(chest -> {
                    chest.getItems().forEach(chestItem -> {
                        if (chestItem.getItemRarity().getName().equals(itemRarity.getName())) {
                            new ChestService(plugin).removeChestItem(chest.getName(), chestItem);
                        }
                    });
                });

                getCache().remove(name);

                getCollection().deleteOne(getItemRarity(name).toDocument());
            });
        }
    }

    public ItemRarity getItemRarity(String name) {
        return getCache().get(name);
    }

    public boolean existItemRarity(String identifier) {
        return getCache().containsKey(identifier);
    }

    public void loadRaritiesIntoCache() {
        getCollection().find().into(new ArrayList<>()).forEach(document -> {
            ItemRarity itemRarity = new ItemRarity().fromDocument(document);
            getCache().putIfAbsent(itemRarity.getName(), itemRarity);
        });
    }

    private MongoCollection<Document> getCollection() {
        return plugin.getMongoDBStorage().getMongoClient().getDatabase("Chests").getCollection("Data_Rarity");
    }

    private Cache<String, ItemRarity> getCache() {
        return plugin.getRarityCache();
    }
}
