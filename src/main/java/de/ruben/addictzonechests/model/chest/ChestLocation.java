package de.ruben.addictzonechests.model.chest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChestLocation {

    private UUID uuid;
    private Location location;
    private String chest;

    public Document toDocument(){
        Document document = new Document("_id", uuid);
        document.append("chest", chest);
        document.append("location", location.serialize());
        return document;
    }

    public ChestLocation fromDocument(Document document){
        this.uuid = document.get("_id", UUID.class);
        this.location = Location.deserialize((Map<String, Object>) document.get("location", Object.class));
        this.chest = document.getString("chest");

        return this;
    }


}
