package de.ruben.addictzonechests.gui;

import com.google.gson.Gson;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.pagination.PaginatedArrayList;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.VoucherService;
import de.ruben.xdevapi.XDevApi;
import de.ruben.xdevapi.custom.gui.ItemPreset;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EditChestGui extends Gui {

    private AddictzoneChests plugin;
    private Chest chest;
    private PaginatedArrayList paginatedArrayList;

    public EditChestGui(AddictzoneChests plugin, Player player, Chest chest) {
        super(6, "§8Kisten Editieren - "+chest.getPrefix()+" §8Kiste", Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));
        this.disableAllInteractions();
        this.plugin = plugin;
        this.chest = chest;
        this.paginatedArrayList = new PaginatedArrayList(chest.getItems(), 28);
        this.getFiller().fillBorder(ItemPreset.fillItem(inventoryClickEvent -> {}));

        this.setItem(49, ItemPreset.closeItem(inventoryClickEvent -> this.close(player)));

        this.setItem(45, ItemBuilder.from(Material.ARROW).name(Component.text("§9Zurück")).asGuiItem(inventoryClickEvent -> {
            new EditGui(player, plugin, new ChestService(plugin).getChests()).open(player);
        }));

        this.setDefaultClickAction(inventoryClickEvent -> {

            inventoryClickEvent.setCancelled(true);

            ItemStack clickedStack = inventoryClickEvent.getCurrentItem();

            if(clickedStack != null && clickedStack.getType() != Material.AIR){
                NBTItem nbtItem = new NBTItem(clickedStack);

                if(nbtItem.hasKey("chestItem")){
                    ChestItem chestItem = new ChestItem().fromDocument(Document.parse(nbtItem.getString("chestItem")));

                    new ChestService(plugin).addChestItem(chest.getName(), chestItem);

                    new EditChestGui(plugin, player, new ChestService(plugin).getChest(chest.getName())).open(player, paginatedArrayList.getPageIndex());

                    clickedStack.setAmount(0);

                }
            }

        });
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

        for(int i = 0; i < 28; i++){
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

            this.addItem(ItemBuilder.from(itemStackToPlace).asGuiItem(inventoryClickEvent -> {
                if(inventoryClickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                    new ChestService(plugin).removeChestItem(chest.getName(), chestItem);
                    new EditChestGui(plugin, player, new ChestService(plugin).getChest(chest.getName())).open(player, paginatedArrayList.getPageIndex());
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast das ChestItem mit der UUID §b"+chestItem.getId()+" §7erfolgreich gelöscht!");
                }
            }));
        }

        if(paginatedArrayList.isNextPageAvailable()){
            this.setItem(50, ItemBuilder.from(Material.ARROW).name(Component.text("§9Nächste Seite")).asGuiItem(inventoryClickEvent -> {
                new EditChestGui(plugin, player, chest).open(player, paginatedArrayList.getPageIndex()+1);
            }));
        }

        if(paginatedArrayList.isPreviousPageAvailable()){
            this.setItem(48, ItemBuilder.from(Material.ARROW).name(Component.text("§9Letzte Seite")).asGuiItem(inventoryClickEvent -> {
                new EditChestGui(plugin, player, chest).open(player, paginatedArrayList.getPageIndex()-1);
            }));
        }

        this.update();
    }
}
