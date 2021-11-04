package de.ruben.addictzonechests.gui;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.pagination.PaginatedArrayList;
import de.ruben.xdevapi.custom.gui.ItemPreset;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EditGui extends Gui {

    private final AddictzoneChests plugin;
    private final PaginatedArrayList paginatedArrayList;
    private final List<Chest> chests;

    public EditGui(Player player, AddictzoneChests plugin, List<Chest> chests) {
        super(6, "Kisten Editieren - Übersicht", Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));
        this.disableAllInteractions();

        this.plugin = plugin;
        this.chests = chests;
        this.paginatedArrayList = new PaginatedArrayList(chests, 28);

        this.getFiller().fillBorder(ItemPreset.fillItem(inventoryClickEvent -> {}));
        this.setItem(49, ItemPreset.closeItem(inventoryClickEvent -> this.close(player)));

        this.setDefaultTopClickAction(event -> event.setCancelled(true));

    }

    @Override
    public void open(@NotNull HumanEntity player) {
        setPageItems((Player) player, 0);
        super.open(player);
    }

    public void open(@NotNull HumanEntity player, int page) {
        setPageItems((Player) player, page);
        super.open(player);
    }

    private void setPageItems(Player player, int page){
        paginatedArrayList.gotoPage(page);

        for(int i = 0; i < 28; i++){
            if (i >= paginatedArrayList.size()) {
                break;
            }

            Chest chest = (Chest) paginatedArrayList.get(i);

            ItemStack itemStack = ItemBuilder
                    .from(Material.CHEST)
                    .name(Component.text("§7"+chest.getPrefix()+" §7Kiste §7(klick)"))
                    .build();

            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setObject("chest", chest.getName());

            this.addItem(new GuiItem(nbtItem.getItem(), inventoryClickEvent -> new EditChestGui(plugin, player, chest).open(player)));

        }

        if(paginatedArrayList.isNextPageAvailable()){
            this.setItem(50, ItemBuilder.from(Material.ARROW).name(Component.text("§9Nächste Seite")).asGuiItem(inventoryClickEvent -> new EditGui(player, plugin, chests).open(player, paginatedArrayList.getPageIndex()+1)));
        }

        if(paginatedArrayList.isPreviousPageAvailable()){
            this.setItem(48, ItemBuilder.from(Material.ARROW).name(Component.text("§9Letzte Seite")).asGuiItem(inventoryClickEvent -> new EditGui(player, plugin, chests).open(player, paginatedArrayList.getPageIndex()-1)));
        }

        this.update();
    }
}
