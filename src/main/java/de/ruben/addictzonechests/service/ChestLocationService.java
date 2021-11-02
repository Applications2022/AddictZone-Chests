package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.ChestLocation;
import org.bson.Document;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChestLocationService {

    private AddictzoneChests plugin;

    public ChestLocationService(AddictzoneChests plugin) {
        this.plugin = plugin;
    }

    public void addChestLocation(String chest, Location location){
        addChestLocation(new ChestLocation(UUID.randomUUID(), location, chest));
    }

    public void addChestLocation(ChestLocation chestLocation){
        getCollection().insertOne(chestLocation.toDocument());
    }

    public void deleteChestLocation(UUID uuid){
        getCollection().deleteOne(Filters.eq("_id", uuid));
    }

    public List<ChestLocation> getLocations(){
        return getCollection().find().into(new ArrayList<>()).stream().map(document -> new ChestLocation().fromDocument(document)).collect(Collectors.toList());
    }



    private MongoCollection<Document> getCollection(){
        return plugin.getMongoDBStorage().getMongoDatabase().getCollection("Data_ChestLocations");
    }
}
