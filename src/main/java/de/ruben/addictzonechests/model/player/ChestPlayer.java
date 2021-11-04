package de.ruben.addictzonechests.model.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChestPlayer {

    private UUID uuid;
    private Map<String, Integer> keys;
    private ChestHistory chestHistory;

}
