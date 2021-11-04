package de.ruben.addictzonechests.listener;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.service.KeyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record JoinQuitListener(AddictzoneChests plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new KeyService(plugin).loadUser(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        new KeyService(plugin).removeUser(event.getPlayer().getUniqueId());
    }
}
