package de.ruben.addictzonechests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import de.ruben.addictzonechests.model.chest.ChestItem;
import org.bson.Document;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChestHistoryEntry {

    private UUID uuid;
    private String chest;
    private Date date;
    private ChestItem win;

    public Document toDocument(){
        Document document = new Document("_id", uuid);
        document.append("chest", chest);
        document.append("date", date);
        document.append("wonItem", win.toDocument());

        return document;
    }

    public ChestHistoryEntry fromDocument(Document document){
        this.uuid = document.get("_id", UUID.class);
        this.chest = document.getString("chest");
        this.date = document.getDate("date");
        this.win = new ChestItem().fromDocument(document.get("wonItem", Document.class));

        return this;
    }

}
