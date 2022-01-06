package de.ruben.addictzonechests.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.xdevapi.XDevApi;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.cache2k.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record KeyService(AddictzoneChests plugin) {

    public void addKey(UUID uuid, String chest, int amount) {

        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            Document user = getUser(uuid);

            Map<String, Integer> keys = user.get("keys", Map.class);

            if (keys.containsKey(chest)) {
                keys.replace(chest, keys.get(chest) + amount);
            } else {
                keys.put(chest, amount);
            }

            getCache().replace(uuid, keys);

            XDevApi.getInstance().getxScheduler().async(() -> {
                user.replace("keys", keys);
                getCollection().replaceOne(Filters.eq("_id", uuid), user);
            });
        } else {
            XDevApi.getInstance().getxScheduler().async(() -> {
                Document user = getUser(uuid);

                Map<String, Integer> keys = user.get("keys", Map.class);

                if (keys.containsKey(chest)) {
                    keys.replace(chest, keys.get(chest) + amount);
                } else {
                    keys.put(chest, amount);
                }

                user.replace("keys", keys);
                getCollection().replaceOne(Filters.eq("_id", uuid), user);
            });
        }
    }

    public void removeKey(UUID uuid, String chest, int amount) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            Document user = getUser(uuid);

            Map<String, Integer> keys = user.get("keys", Map.class);

            if (keys.containsKey(chest)) {
                keys.replace(chest, keys.get(chest) - amount);
            } else {
                keys.put(chest, 0);
            }

            getCache().replace(uuid, keys);

            XDevApi.getInstance().getxScheduler().async(() -> {
                user.replace("keys", keys);
                getCollection().replaceOne(Filters.eq("_id", uuid), user);
            });
        } else {
            XDevApi.getInstance().getxScheduler().async(() -> {
                Document user = getUser(uuid);

                Map<String, Integer> keys = user.get("keys", Map.class);

                if (keys.containsKey(chest)) {
                    keys.replace(chest, keys.get(chest) - amount);
                } else {
                    keys.put(chest, 0);
                }

                user.replace("keys", keys);
                getCollection().replaceOne(Filters.eq("_id", uuid), user);
            });
        }
    }

    public void setKey(UUID uuid, String chest, int amount) {

        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            Document user = getUser(uuid);

            Map<String, Integer> keys = user.get("keys", Map.class);

            if (keys.containsKey(chest)) {
                keys.replace(chest, amount);
            } else {
                keys.put(chest, amount);
            }

            getCache().replace(uuid, keys);

            XDevApi.getInstance().getxScheduler().async(() -> {
                user.replace("keys", keys);
                getCollection().replaceOne(Filters.eq("_id", uuid), user);
            });
        } else {
            XDevApi.getInstance().getxScheduler().async(() -> {
                Document user = getUser(uuid);

                Map<String, Integer> keys = user.get("keys", Map.class);

                if (keys.containsKey(chest)) {
                    keys.replace(chest, amount);
                } else {
                    keys.put(chest, amount);
                }

                user.replace("keys", keys);
                getCollection().replaceOne(Filters.eq("_id", uuid), user);
            });
        }
    }

    public Integer getKeys(UUID uuid, String chest) {
        return getKeys(uuid).getOrDefault(chest, 0);
    }

    public Map<String, Integer> getKeys(UUID uuid) {
        return getUser(uuid).get("keys", Map.class);
    }

    public Document getUser(UUID uuid) {
        if (!existUser(uuid)) createUser(uuid);

        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            return new Document("_id", uuid).append("keys", getCache().get(uuid));
        } else {
            return getCollection().find(Filters.eq("_id", uuid)).first();
        }
    }

    public void loadUser(UUID uuid) {
        if (getCollection().find(Filters.eq("_id", uuid)).first() == null) {
            createUser(uuid);
        } else {
            Document user = getCollection().find(Filters.eq("_id", uuid)).first();
            Map<String, Integer> keys = user.get("keys", Map.class);
            getCache().put(uuid, keys);
        }
    }

    public void removeUser(UUID uuid) {
        getCache().remove(uuid);
    }

    public void createUser(UUID uuid) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            getCache().put(uuid, new HashMap<>());
        }
        getCollection().insertOne(new Document("_id", uuid).append("keys", new HashMap<>()));
    }

    public boolean existUser(UUID uuid) {
        if (!getCache().containsKey(uuid)) {
            return getCollection().find(Filters.eq("_id", uuid)).first() != null;
        } else {
            return true;
        }
    }

    private MongoCollection<Document> getCollection() {
        return plugin.getMongoDBStorage().getMongoClient().getDatabase("Chests").getCollection("Data_ChestKeys");
    }

    private Cache<UUID, Map> getCache() {

        return plugin.getKeyCache();
    }

}
