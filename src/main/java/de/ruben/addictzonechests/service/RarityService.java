package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.ItemRarity;
import de.ruben.xdevapi.XDevApi;
import org.bson.Document;
import org.bukkit.Bukkit;

public class RarityService {

    private AddictzoneChests plugin;

    public RarityService(AddictzoneChests plugin) {
        this.plugin = plugin;
    }

    public ItemRarity createItemRarity(String name, String prefix, String message, Integer weight){

        if(existItemRarity(name)){
            return getItemRarity(name);
        }

        ItemRarity itemRarity = new ItemRarity(name, prefix, message, weight);

        getCollection().insertOne(itemRarity.toDocument());

        return itemRarity;

    }

    public void deleteItemRarity(String name){
        if(existItemRarity(name)){
            getCollection().deleteOne(getItemRarity(name).toDocument());
        }
    }

    public void deleteItemRarityAndFormatItems(String name){
        if(existItemRarity(name)){

            XDevApi.getInstance().getxScheduler().async(() -> {
                ItemRarity itemRarity = getItemRarity(name);

                new ChestService(plugin).getChests().forEach(chest -> {
                    chest.getItems().forEach(chestItem -> {
                        if(chestItem.getItemRarity().getName().equals(itemRarity.getName())){
                            new ChestService(plugin).removeChestItem(chest.getName(), chestItem);
                        }
                    });
                });

                getCollection().deleteOne(getItemRarity(name).toDocument());
            });
        }
    }

    public ItemRarity getItemRarity(String name) {
        return new ItemRarity().fromDocument(getCollection().find(Filters.eq("_id", name)).first());
    }

    public boolean existItemRarity(String identifier){
        return getCollection().find(Filters.eq("_id", identifier)).first() != null;
    }

    private MongoCollection<Document> getCollection(){
        return plugin.getMongoDBStorage().getMongoDatabase().getCollection("Data_Rarity");
    }
}
