package de.ruben.addictzonechests;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import de.ruben.addictzonechests.command.KistenCommand;
import de.ruben.addictzonechests.command.VoucherCommand;
import de.ruben.addictzonechests.listener.BlockKlickListener;
import de.ruben.addictzonechests.listener.DefaultChestRewardListener;
import de.ruben.addictzonechests.listener.JoinQuitListener;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ItemRarity;
import de.ruben.addictzonechests.service.ChestLocationService;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.RarityService;
import de.ruben.xdevapi.XDevApi;
import de.ruben.xdevapi.storage.MongoDBStorage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class AddictzoneChests extends JavaPlugin {

    private static AddictzoneChests instance;

    private LuckPerms luckperms;

    private MongoDBStorage mongoDBStorage;

    private ScheduledExecutorService executorService;

    private Cache<String, ItemRarity> rarityCache;

    private Cache<UUID, Map> keyCache;

    private Cache<String, Chest> chestCache;

    @Override
    public void onEnable() {
        instance = this;
        this.luckperms =  LuckPermsProvider.get();
//        this.mongoDBStorage = new MongoDBStorage(XDevApi.getInstance(), "localhost", "Currency", 27017, MongoClientOptions.builder().codecRegistry(CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry())).build());
        this.mongoDBStorage = new MongoDBStorage(XDevApi.getInstance(), 10, "localhost", "Currency", 27017, "currency", "wrgO4FTbV6UyLwtMzfsp", MongoClientOptions.builder().codecRegistry(CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry())).build());

        this.executorService = Executors.newScheduledThreadPool(5);
        this.rarityCache = Cache2kBuilder
                        .of(String.class, ItemRarity.class)
                        .name("rarityCache")
                        .eternal(true)
                        .entryCapacity(150)
                        .build();

        this.keyCache = Cache2kBuilder
                .of(UUID.class, Map.class)
                .name("keyCache")
                .eternal(true)
                .entryCapacity(150)
                .build();

        this.chestCache = Cache2kBuilder
                .of(String.class, Chest.class)
                .name("chestCache")
                .eternal(true)
                .entryCapacity(150)
                .build();

        mongoDBStorage.connect();

        new KistenCommand(this);
        new VoucherCommand(this);

        Bukkit.getPluginManager().registerEvents(new DefaultChestRewardListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockKlickListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this), this);

        new ChestLocationService(this).getLocations().forEach(chestLocation -> {
            Location location = chestLocation.getLocation();

            Block block = location.getBlock();

            if(block != null && block.getType() != Material.AIR){
                block.setMetadata("chestBlock", new FixedMetadataValue(this, chestLocation));
            }else{
                new ChestLocationService(this).deleteChestLocation(chestLocation.getUuid());
            }
        });

        new RarityService(this).loadRaritiesIntoCache();
        new ChestService(this).loadChestsIntoCache();


    }

    @Override
    public void onDisable() {
        getRarityCache().clearAndClose();
        getKeyCache().clearAndClose();
        getChestCache().clearAndClose();
    }

    public MongoDBStorage getMongoDBStorage() {
        return mongoDBStorage;
    }

    public static AddictzoneChests getInstance() {
        return instance;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public LuckPerms getLuckperms() {
        return luckperms;
    }

    public Cache<String, ItemRarity> getRarityCache() {
        return rarityCache;
    }

    public Cache<UUID, Map> getKeyCache() {
        return keyCache;
    }

    public Cache<String, Chest> getChestCache() {
        return chestCache;
    }
}