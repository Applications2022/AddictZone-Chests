package de.ruben.addictzonechests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import de.ruben.addictzonechests.model.chest.ChestItem;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChestHistoryEntry {

    private String chest;
    private Date date;
    private ChestItem win;

}
