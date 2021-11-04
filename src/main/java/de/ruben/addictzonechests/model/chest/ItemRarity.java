package de.ruben.addictzonechests.model.chest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRarity {

    private @NotNull String name;
    private @NotNull String prefix;
    private @NotNull String message;
    private @NotNull Integer weight;
    private @NotNull Boolean broadcast;

    public ItemRarity fromDocument(Document document){
        this.name = document.getString("_id");
        this.prefix = document.getString("prefix");
        this.message = document.getString("message");
        this.weight = document.getInteger("weight");
        this.broadcast = document.containsKey("broadcast") ? document.getBoolean("broadcast") : false;

        return this;
    }

    public Document toDocument(){
        Document document = new Document("_id", name);
        document.append("prefix", prefix);
        document.append("message", message);
        document.append("weight", weight);
        document.append("broadcast", broadcast);
        return document;
    }
}
