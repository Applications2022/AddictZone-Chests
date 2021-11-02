package de.ruben.addictzonechests.event;

import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChestRewardEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Player player;
    private Chest chest;
    private ChestItem win;

    private boolean isCancelled;

    public ChestRewardEvent(boolean asnyc, Player player, Chest chest, ChestItem win) {
        super(asnyc);
        this.player = player;
        this.chest = chest;
        this.win = win;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
