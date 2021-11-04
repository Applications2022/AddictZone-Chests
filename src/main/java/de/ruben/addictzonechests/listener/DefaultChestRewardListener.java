package de.ruben.addictzonechests.listener;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.event.ChestRewardEvent;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.service.ChestHistoryService;
import de.ruben.addictzonechests.service.VoucherService;
import de.ruben.xdevapi.XDevApi;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Date;

public record DefaultChestRewardListener(AddictzoneChests plugin) implements Listener {

    @EventHandler
    public void onChestRewardEvent(ChestRewardEvent event) {
        Player player = event.getPlayer();
        ChestItem chestItem = event.getWin();

        if (new VoucherService().isVoucher(chestItem.getItemStack())) {
            new VoucherService().getVoucher(chestItem.getItemStack()).onWin(player, chestItem.getItemStack());
        } else {
            Bukkit.broadcast(Component.text(player.getName() + " hat gewonnen! Aus der Chest: " + event.getChest().getPrefix()));
            player.getInventory().addItem(chestItem.getItemStack());
        }

        XDevApi.getInstance().getxScheduler().async(() -> {
            new ChestHistoryService(plugin).addHistory(player.getUniqueId(), event.getChest().getPrefix(), new Date(System.currentTimeMillis()), chestItem);
        });

    }
}
