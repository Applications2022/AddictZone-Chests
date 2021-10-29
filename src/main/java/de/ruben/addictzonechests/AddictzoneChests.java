package de.ruben.addictzonechests;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import de.ruben.addictzonechests.command.KistenCommand;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.RarityService;
import de.ruben.xdevapi.XDevApi;
import de.ruben.xdevapi.storage.MongoDBStorage;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bson.codecs.configuration.CodecRegistries;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class AddictzoneChests extends JavaPlugin {

    private static AddictzoneChests instance;

    private MongoDBStorage mongoDBStorage;

    @Override
    public void onEnable() {
        this.instance = this;
        this.mongoDBStorage = new MongoDBStorage(XDevApi.getInstance(), "localhost", "Currency", 27017, MongoClientOptions.builder().codecRegistry(CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry())).build());
//        this.mongoDBStorage = new MongoDBStorage(XDevApi.getInstance(), 10, "localhost", "Currency", 27017, "currency", "wrgO4FTbV6UyLwtMzfsp", MongoClientOptions.builder().codecRegistry(CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry())).build());

        mongoDBStorage.connect();

        new KistenCommand(this);

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
}