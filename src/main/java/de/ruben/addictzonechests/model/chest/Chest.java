package de.ruben.addictzonechests.model.chest;

import de.ruben.xdevapi.util.global.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chest {

    private @NotNull UUID id;
    private @NotNull String name;
    private @NotNull String prefix;
    private @NotNull List<ChestItem> items;

    public List<ChestItem> getChestItemsWithRarity(String rarityIdentifier){
        return items.stream().filter(chestItem -> chestItem.getItemRarityIdentifier().equalsIgnoreCase(rarityIdentifier)).collect(Collectors.toList());
    }

    public RandomUtil.RandomCollection<ChestItem> getWeightedChestItemList(){

        RandomUtil.RandomCollection<ChestItem> randomCollection = new RandomUtil.RandomCollection();

        items.forEach(chestItem -> {
            randomCollection.add(chestItem.getItemRarity().getWeight(), chestItem);
        });

        return randomCollection;

    }


    public Map<String, List<ChestItem>> getChestItemsGroupedByRarity(){

        Map<String, List<ChestItem>> groupedItems = new HashMap<>();

        items.forEach(chestItem -> {
            List<ChestItem> rarityList;

            if(groupedItems.containsKey(chestItem.getItemRarityIdentifier())){
                rarityList = groupedItems.get(chestItem.getItemRarityIdentifier());
            }else{
                rarityList = new ArrayList<>();
            }

            rarityList.add(chestItem);

            if(groupedItems.containsKey(chestItem.getItemRarityIdentifier())){
                groupedItems.replace(chestItem.getItemRarityIdentifier(), rarityList);
            }else{
                groupedItems.put(chestItem.getItemRarityIdentifier(), rarityList);
            }


        });

        return groupedItems;

    }


    public Chest fromDocument(Document document){
        this.id = document.get("_id", UUID.class);
        this.name = document.getString("name");
        this.prefix = document.getString("prefix");

        List<Document> items = document.getList("items", Document.class);

        this.items = items.stream().map(document1 -> new ChestItem().fromDocument(document1)).collect(Collectors.toList());

        return this;
    }

    public Document toDocument(){
        Document document = new Document("_id", id);
        document.append("name", name);
        document.append("prefix", prefix);

        document.append("items", items.stream().map(ChestItem::toDocument).collect(Collectors.toList()));

        return document;
    }

}
