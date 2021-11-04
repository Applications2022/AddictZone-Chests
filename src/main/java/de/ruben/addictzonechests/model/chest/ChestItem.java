package de.ruben.addictzonechests.model.chest;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.service.RarityService;
import de.ruben.addictzonechests.util.BukkitSerialization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChestItem {

    private UUID id;
    private Map<String, Object> itemStack;
    private ItemRarity itemRarity;

    public ChestItem(UUID id, Map<String, Object> itemStack, String itemRarity) {
        this.id = id;
        this.itemStack = itemStack;
        ItemRarity itemRarityFromDB = new RarityService(AddictzoneChests.getInstance()).getItemRarity(itemRarity);
        this.itemRarity = itemRarityFromDB != null ? itemRarityFromDB : new ItemRarity("§7Unbekannter Seltenheitstyp!", "§7Unbekannter Seltenheitstyp!", "", 1, false);
    }

    public ItemStack getItemStack(){
        return ItemStack.deserialize(itemStack);
    }

    public void setItemStack(ItemStack itemStack){
        this.itemStack = itemStack.serialize();
    }

    public String getItemRarityIdentifier(){
        return itemRarity.getName();
    }

    public ChestItem fromDocument(Document document){
        this.id = document.get("_id", UUID.class);

        try {
            this.itemStack = BukkitSerialization.itemStackArrayFromBase64(document.getString("itemStack")).serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ItemRarity itemRarityFromDB = new RarityService(AddictzoneChests.getInstance()).getItemRarity(document.getString("itemRarityIdentifier"));
        this.itemRarity = itemRarityFromDB != null ? itemRarityFromDB : new ItemRarity("§7Unbekannter Seltenheitstyp!", "§7Unbekannter Seltenheitstyp!", "", 1, false);

        return this;
    }

    public Document toDocument(){
        Document document = new Document("_id", id);

        document.append("itemStack", BukkitSerialization.itemStackArrayToBase64(ItemStack.deserialize(this.itemStack)));
        document.append("itemRarityIdentifier", itemRarity.getName());

        return document;
    }

}
