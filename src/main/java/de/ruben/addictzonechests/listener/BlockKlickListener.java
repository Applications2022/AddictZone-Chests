package de.ruben.addictzonechests.listener;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.gui.ChestPreviewGui;
import de.ruben.addictzonechests.gui.ItemPreviewGui;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestLocation;
import de.ruben.addictzonechests.service.ChestService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockKlickListener implements Listener {

    @EventHandler
    public void onBlockCklick(PlayerInteractEvent event){
        Block block = event.getClickedBlock();

        if(block != null && block.getType() != Material.AIR && event.getHand() == EquipmentSlot.HAND){
            if(block.hasMetadata("chestBlock")) {
                event.setCancelled(true);

                ChestLocation chestLocation = (ChestLocation) block.getMetadata("chestBlock").get(0).value();
                Chest chest = new ChestService(AddictzoneChests.getInstance()).getChest(chestLocation.getChest());

                if(event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    new ItemPreviewGui(AddictzoneChests.getInstance(), event.getPlayer(), chest, block).open(event.getPlayer());
                }else{
                    new ChestPreviewGui(AddictzoneChests.getInstance(), event.getPlayer(), chest, block).open(event.getPlayer());
                }
            }

        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getBlock().hasMetadata("chestBlock")){
            event.setCancelled(true);
        }
    }

}
