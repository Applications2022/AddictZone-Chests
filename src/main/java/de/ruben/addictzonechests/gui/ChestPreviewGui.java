package de.ruben.addictzonechests.gui;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.service.KeyService;
import de.ruben.xdevapi.XDevApi;
import de.ruben.xdevapi.custom.gui.ItemPreset;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public class ChestPreviewGui extends Gui {
    public ChestPreviewGui(AddictzoneChests plugin, Player player, Chest chest, Block block) {
        super(3, chest.getPrefix()+" §bKiste", Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));

        this.disableAllInteractions();
        this.getFiller().fill(ItemPreset.fillItem(inventoryClickEvent -> {}));

        this.setItem(13,
                ItemBuilder
                .from(block.getType())
                .name(Component.text(chest.getPrefix()+" §bKiste"))
                .lore(
                        Component.text(" "),
                        Component.text("§7Du besitzt §b"+ XDevApi.getInstance().getxUtil().getStringUtil().moneyFormat(new KeyService(plugin).getKeys(player.getUniqueId(), chest.getName())) +" §7Schlüssel!")

                )
                .asGuiItem(event -> {

                    Integer keys = new KeyService(plugin).getKeys(player.getUniqueId(), chest.getName());

                    if(keys > 0){

                        new KeyService(plugin).removeKey(player.getUniqueId(), chest.getName(), 1);

                        new AnimationGui(plugin, player, chest, block).open(player);

                    }else{
                        this.close(player);
                        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§cDazu hast nicht genug Schlüssel!");
                    }

                })
        );

        this.setItem(26,
                ItemBuilder
                        .from(Material.ENDER_EYE)
                        .name(Component.text("§bVorschau"))
                        .asGuiItem(event -> new ItemPreviewGui(plugin, player, chest, block).open(player))
                );

        this.setDefaultTopClickAction(event -> event.setCancelled(true));

    }
}
