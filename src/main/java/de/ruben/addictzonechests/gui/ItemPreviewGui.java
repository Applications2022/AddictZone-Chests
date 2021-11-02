package de.ruben.addictzonechests.gui;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.VoucherService;
import de.ruben.xcore.pagination.PaginatedArrayList;
import de.ruben.xdevapi.custom.gui.ItemPreset;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemPreviewGui extends Gui {

    private AddictzoneChests plugin;
    private PaginatedArrayList paginatedArrayList;
    private Chest chest;
    private Block block;

    public ItemPreviewGui(AddictzoneChests plugin, Player player, Chest chest, Block block) {
        super(6, "Kisten Vorschau §b- §8"+chest.getPrefix()+" §8Kiste", Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));

        this.plugin = plugin;
        this.paginatedArrayList = new PaginatedArrayList(chest.getItems().stream()
                .sorted(Comparator.comparing(o -> o.getItemRarity().getWeight()))
                .collect(Collectors.toList()), 28);
        this.chest = chest;
        this.block = block;

        this.disableAllInteractions();
        this.getFiller().fillBorder(ItemPreset.fillItem(inventoryClickEvent -> {}));
        this.setItem(49, ItemPreset.closeItem(inventoryClickEvent -> this.close(player)));

        this.setItem(45, ItemBuilder.from(Material.ARROW).name(Component.text("§9Zurück")).asGuiItem(inventoryClickEvent -> {
            new ChestPreviewGui(plugin, player, chest, block).open(player);
        }));
    }

    @Override
    public void open(@NotNull HumanEntity player) {
        super.open(player);

        setPageItems((Player) player, 0);
    }

    public void open(@NotNull HumanEntity player, int page) {
        super.open(player);

        setPageItems((Player) player, page);
    }

    private void setPageItems(Player player, int page){
        paginatedArrayList.gotoPage(page);

        for(int i = 0; i < 28; i++) {
            if (i >= paginatedArrayList.size()) {
                break;
            }

            ChestItem chestItem = (ChestItem) paginatedArrayList.get(i);

            NBTItem nbtItem = new NBTItem(chestItem.getItemStack());

            nbtItem.setString("id", chestItem.getId().toString());

            ItemStack itemStackToPlace = nbtItem.getItem();
            ItemMeta itemMeta = itemStackToPlace.getItemMeta();

            List<Component> lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();
            lore.add(Component.text(" "));
            lore.add(Component.text("§7➥ Seltenheit: "+ ChatColor.translateAlternateColorCodes('&', chestItem.getItemRarity().getPrefix())));
            lore.add(Component.text("§7➥ Typ: §b"+(new VoucherService().isVoucher(chestItem.getItemStack()) ? "Gutschein" : "Item")));

            itemMeta.lore(lore);

            itemStackToPlace.setItemMeta(itemMeta);


            this.addItem(ItemBuilder.from(itemStackToPlace).asGuiItem());

            if(paginatedArrayList.isNextPageAvailable()){
                this.setItem(50, ItemBuilder.from(Material.ARROW).name(Component.text("§9Nächste Seite")).asGuiItem(inventoryClickEvent -> {
                    new ItemPreviewGui(plugin, player, chest, block).open(player, paginatedArrayList.getPageIndex()+1);
                }));
            }

            if(paginatedArrayList.isPreviousPageAvailable()){
                this.setItem(48, ItemBuilder.from(Material.ARROW).name(Component.text("§9Letzte Seite")).asGuiItem(inventoryClickEvent -> {
                    new ItemPreviewGui(plugin, player, chest, block).open(player, paginatedArrayList.getPageIndex()-1);
                }));
            }

            this.update();
        }


    }
}
