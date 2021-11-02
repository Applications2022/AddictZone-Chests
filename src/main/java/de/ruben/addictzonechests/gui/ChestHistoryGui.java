package de.ruben.addictzonechests.gui;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.model.player.ChestHistoryEntry;
import de.ruben.addictzonechests.pagination.PaginatedArrayList;
import de.ruben.addictzonechests.service.ChestHistoryService;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.VoucherService;
import de.ruben.xdevapi.XDevApi;
import de.ruben.xdevapi.custom.gui.ItemPreset;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ChestHistoryGui extends Gui {

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");

    private AddictzoneChests plugin;
    private PaginatedArrayList paginatedArrayList;
    private ChestHistoryService chestHistoryService;
    private ChestService chestService;
    private SortType sortType;
    private UUID target;

    public ChestHistoryGui(AddictzoneChests plugin, Player player, SortType sortType) {
        super(6, "§8Kisten Historie", Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));

        this.plugin = plugin;
        this.sortType = sortType;
        this.chestHistoryService = new ChestHistoryService(plugin);
        this.paginatedArrayList = new PaginatedArrayList(getChestHistorySorted(player.getUniqueId(), sortType), 28);
        this.chestService = new ChestService(plugin);
        this.target = null;

        this.disableAllInteractions();
        this.getFiller().fillBorder(ItemPreset.fillItem(inventoryClickEvent -> {}));
        this.setItem(49, ItemPreset.closeItem(inventoryClickEvent -> this.close(player)));
    }

    public ChestHistoryGui(AddictzoneChests plugin, Player player, UUID target, SortType sortType) {
        super(6, "§8Kisten Historie §b- §8"+ Bukkit.getOfflinePlayer(target).getName(), Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));

        this.plugin = plugin;
        this.sortType = sortType;
        this.chestHistoryService = new ChestHistoryService(plugin);
        this.paginatedArrayList = new PaginatedArrayList(getChestHistorySorted(target, sortType), 28);
        this.chestService = new ChestService(plugin);
        this.target = target;


        this.disableAllInteractions();
        this.getFiller().fillBorder(ItemPreset.fillItem(inventoryClickEvent -> {}));
        this.setItem(49, ItemPreset.closeItem(inventoryClickEvent -> this.close(player)));

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

            ChestHistoryEntry chestHistoryEntry = ((ChestHistoryEntry) paginatedArrayList.get(i));

            ChestItem chestItem = chestHistoryEntry.getWin();

            ItemStack itemStack = chestItem.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();

            long seconds = (System.currentTimeMillis()-chestHistoryEntry.getDate().getTime())/1000;

            String timeString = XDevApi.getInstance().getxUtil().getGlobal().getTimeUtil().convertSecondsHM((int) seconds);

            timeString = timeString.startsWith(" ") ? timeString : " "+timeString;

            List<Component> lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();
            lore.add(Component.text(" "));
            lore.add(Component.text("§7➥ Seltenheit: "+ ChatColor.translateAlternateColorCodes('&', chestItem.getItemRarity().getPrefix())));
            lore.add(Component.text("§7➥ Typ: §b"+(new VoucherService().isVoucher(chestItem.getItemStack()) ? "Gutschein" : "Item")));
            lore.add(Component.text("§7➥ Kiste: §b"+(chestService.existChest(chestHistoryEntry.getChest()) ? chestService.getChest(chestHistoryEntry.getChest()).getPrefix() : chestHistoryEntry.getChest())));
            lore.add(Component.text(" "));
            lore.add(Component.text("§8Gezogen am §b"+dateFormat.format(chestHistoryEntry.getDate())+"Uhr §8(vor"+timeString+")"));

            itemMeta.lore(lore);

            itemStack.setItemMeta(itemMeta);

            this.addItem(ItemBuilder.from(itemStack).asGuiItem());


            this.setItem(53, getSotingItem(player, this.sortType));

            if(paginatedArrayList.isNextPageAvailable()){
                this.setItem(50, ItemBuilder.from(Material.ARROW).name(Component.text("§9Nächste Seite")).asGuiItem(inventoryClickEvent -> {
                    if(target == null){
                        new ChestHistoryGui(plugin, player, sortType).open(player, paginatedArrayList.getPageIndex()+1);
                    }else{
                        new ChestHistoryGui(plugin, player, target, sortType).open(player, paginatedArrayList.getPageIndex()+1);
                    }
                }));
            }

            if(paginatedArrayList.isPreviousPageAvailable()){
                this.setItem(48, ItemBuilder.from(Material.ARROW).name(Component.text("§9Letzte Seite")).asGuiItem(inventoryClickEvent -> {
                    if(target == null){
                        new ChestHistoryGui(plugin, player, sortType).open(player, paginatedArrayList.getPageIndex()-1);
                    }else{
                        new ChestHistoryGui(plugin, player, target, sortType).open(player, paginatedArrayList.getPageIndex()-1);
                    }
                }));
            }

            this.update();
        }
    }


    private List<ChestHistoryEntry> getChestHistorySorted(UUID uuid, SortType sortType){
        switch (sortType){
            case RARITY_ASCENDING:
                return chestHistoryService.getHistory(uuid)
                    .stream()
                    .sorted(Comparator.comparing(o -> o.getWin().getItemRarity().getWeight()))
                    .collect(Collectors.toList());
            case RARITY_DESCENDING:
                return chestHistoryService.getHistory(uuid)
                        .stream()
                        .sorted((o1, o2) -> o2.getWin().getItemRarity().getWeight().compareTo(o1.getWin().getItemRarity().getWeight()))
                        .collect(Collectors.toList());
            case DATUM_ASCENDING:
                return chestHistoryService.getHistory(uuid)
                        .stream()
                        .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                        .collect(Collectors.toList());
            case DATUM_DESCENDING:
                return chestHistoryService.getHistory(uuid)
                        .stream()
                        .sorted(Comparator.comparing(ChestHistoryEntry::getDate))
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }

    }

    private GuiItem getSotingItem(Player player, SortType sortType){

        return ItemBuilder
                .from(Material.FILLED_MAP)
                .name(Component.text("§bSortierung"))
                .lore(
                        Component.text(" "),
                        Component.text((sortType == SortType.DATUM_ASCENDING ? "§b" : "§7")+"Neuste zuerst"),
                        Component.text((sortType == SortType.DATUM_DESCENDING ? "§b" : "§7")+"Älteste zuerst"),
                        Component.text((sortType == SortType.RARITY_ASCENDING ? "§b" : "§7")+"Seltenste zuerst"),
                        Component.text((sortType == SortType.RARITY_DESCENDING ? "§b" : "§7")+"Häufigste zuerst"),
                        Component.text(" ")

                )
                .asGuiItem(event -> {



                    switch (sortType){
                        case DATUM_ASCENDING:
                            System.out.println("1");
                            if(target == null){
                                new ChestHistoryGui(plugin, player, SortType.DATUM_DESCENDING).open(player, paginatedArrayList.getPageIndex());
                            }else{
                                new ChestHistoryGui(plugin, player, target, SortType.DATUM_DESCENDING).open(player, paginatedArrayList.getPageIndex());
                            }
                            break;

                        case DATUM_DESCENDING:
                            System.out.println("2");
                            if(target == null){
                                new ChestHistoryGui(plugin, player, SortType.RARITY_ASCENDING).open(player, paginatedArrayList.getPageIndex());
                            }else{
                                new ChestHistoryGui(plugin, player, target, SortType.RARITY_ASCENDING).open(player, paginatedArrayList.getPageIndex());
                            }
                            break;
                        case RARITY_ASCENDING:
                            System.out.println("3");
                            if(target == null){
                                new ChestHistoryGui(plugin, player, SortType.RARITY_DESCENDING).open(player, paginatedArrayList.getPageIndex());
                            }else{
                                new ChestHistoryGui(plugin, player, target, SortType.RARITY_DESCENDING).open(player, paginatedArrayList.getPageIndex());
                            }
                            break;
                        case RARITY_DESCENDING:
                            System.out.println("4");
                            if(target == null){
                                new ChestHistoryGui(plugin, player, SortType.DATUM_ASCENDING).open(player, paginatedArrayList.getPageIndex());
                            }else{
                                new ChestHistoryGui(plugin, player, target, SortType.DATUM_ASCENDING).open(player, paginatedArrayList.getPageIndex());
                            }
                            break;
                    }
                });

    }

    public enum SortType{
        DATUM_ASCENDING,
        DATUM_DESCENDING,
        RARITY_ASCENDING,
        RARITY_DESCENDING;
    }
}
