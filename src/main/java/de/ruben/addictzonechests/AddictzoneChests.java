package de.ruben.addictzonechests;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import de.ruben.addictzonechests.command.KistenCommand;
import de.ruben.addictzonechests.command.VoucherCommand;
import de.ruben.addictzonechests.listener.BlockKlickListener;
import de.ruben.addictzonechests.listener.DefaultChestRewardListener;
import de.ruben.addictzonechests.service.ChestLocationService;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class AddictzoneChests extends JavaPlugin {

    private static AddictzoneChests instance;

    private LuckPerms luckperms;

    private MongoDBStorage mongoDBStorage;

    private ScheduledExecutorService executorService;

    @Override
    public void onEnable() {
        this.instance = this;
        this.luckperms =  LuckPermsProvider.get();
//        this.mongoDBStorage = new MongoDBStorage(XDevApi.getInstance(), "localhost", "Currency", 27017, MongoClientOptions.builder().codecRegistry(CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry())).build());
        this.mongoDBStorage = new MongoDBStorage(XDevApi.getInstance(), 10, "localhost", "Currency", 27017, "currency", "wrgO4FTbV6UyLwtMzfsp", MongoClientOptions.builder().codecRegistry(CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry())).build());

        this.executorService = Executors.newScheduledThreadPool(5);

        mongoDBStorage.connect();

        new KistenCommand(this);
        new VoucherCommand(this);

        Bukkit.getPluginManager().registerEvents(new DefaultChestRewardListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockKlickListener(), this);

        new ChestLocationService(this).getLocations().forEach(chestLocation -> {
            Location location = chestLocation.getLocation();

            Block block = location.getBlock();

            if(block != null && block.getType() != Material.AIR){
                block.setMetadata("chestBlock", new FixedMetadataValue(this, chestLocation));
            }else{
                new ChestLocationService(this).deleteChestLocation(chestLocation.getUuid());
            }
        });

//        new RarityService(this).createItemRarity("normal", "§7Normal", "Normales Item!", 100);
//
//        for(int i = 0; i < 10; ++i){
//
//            new ChestService(this).createChest("Test #"+i);
//
//            for(int y = 0; y < 100; ++y){
//                new ChestService(this).addChestItem("Test #"+i, new ChestItem(UUID.randomUUID(), ItemBuilder.from(Material.ACACIA_SIGN).name(Component.text("Hi Nr."+y)).build().serialize(), "normal"));
//            }
//        }



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
}