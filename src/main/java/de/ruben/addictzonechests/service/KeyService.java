package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.ruben.addictzonechests.AddictzoneChests;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.K;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyService {

    private AddictzoneChests plugin;

    public KeyService(AddictzoneChests plugin){
        this.plugin = plugin;
    }

    public void addKey(UUID uuid, String chest, int amount){

        Document user = getUser(uuid);
        Map<String, Integer> keys = user.get("keys", Map.class);

        if(keys.containsKey(chest)){
            keys.replace(chest, keys.get(chest)+amount);
        }else{
            keys.put(chest, amount);
        }

        user.replace("keys", keys);

        getCollection().replaceOne(Filters.eq("_id", uuid), user);

//        Document user = getUser(uuid);
//
//        if(user.containsKey(chest)){
//            user.append(chest, amount);
//        }else{
//            int currentAmount = user.getInteger(chest, 0);
//            user.replace(chest, currentAmount+amount);
//        }
//
//        getCollection().replaceOne(Filters.eq("_id",uuid), user);
    }

    public void removeKey(UUID uuid, String chest, int amount){

        Document user = getUser(uuid);
        Map<String, Integer> keys = user.get("keys", Map.class);

        if(keys.containsKey(chest)){
            keys.replace(chest, keys.get(chest)-amount);
        }else{
            keys.put(chest, 0);
        }

        user.replace("keys", keys);

        getCollection().replaceOne(Filters.eq("_id", uuid), user);
//        Document user = getUser(uuid);
//
//        if(user.containsKey(chest)){
//            user.append(chest, 0);
//        }else{
//            int currentAmount = user.getInteger(chest, 0);
//            user.replace(chest, currentAmount-amount);
//        }
//
//        getCollection().replaceOne(Filters.eq("_id", uuid), user);
    }

    public void setKey(UUID uuid, String chest, int amount){
        Document user = getUser(uuid);
        Map<String, Integer> keys = user.get("keys", Map.class);

        if(keys.containsKey(chest)){
            keys.replace(chest, amount);
        }else{
            keys.put(chest, amount);
        }

        user.replace("keys", keys);

//        if(user.containsKey(chest)){
//            user.append(chest, amount);
//        }else{
//            user.replace(chest, amount);
//        }
//
        getCollection().replaceOne(Filters.eq("_id", uuid), user);
    }

    public Integer getKeys(UUID uuid, String chest){

//        if(!existUser(uuid)) createUser(uuid);
//
//        return getUser(uuid).getInteger(chest, 0);
        return getKeys(uuid).getOrDefault(chest, 0);
    }

    public Map<String, Integer> getKeys(UUID uuid){
        if(!existUser(uuid)) createUser(uuid);
        return getUser(uuid).get("keys", Map.class);

//        Map<String, Integer> map = new HashMap<>();
//
//        user.forEach((s, o) -> map.put(s, (Integer) o));
//
//        return map;
    }

    public Document getUser(UUID uuid){
        if(!existUser(uuid)) createUser(uuid);
        return getCollection().find(Filters.eq("_id", uuid)).first();
    }

    public void createUser(UUID uuid){
        getCollection().insertOne(new Document("_id", uuid).append("keys", new HashMap<>()));
    }

    public boolean existUser(UUID uuid){
        return getCollection().find(Filters.eq("_id", uuid)).first() != null;
    }

    private MongoCollection<Document> getCollection(){
        return plugin.getMongoDBStorage().getMongoDatabase().getCollection("Data_ChestKeys");
    }

}
