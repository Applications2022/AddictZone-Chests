package de.ruben.addictzonechests.gui;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.event.ChestRewardEvent;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.service.VoucherService;
import de.ruben.xdevapi.XDevApi;
import de.ruben.xdevapi.custom.gui.ItemPreset;
import de.ruben.xdevapi.util.global.RandomUtil;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationGui extends Gui {

    private RandomUtil.RandomCollection<ChestItem> weightedItemList;

    private AddictzoneChests plugin;

    private ScheduledFuture<?> scheduledFuture;

    private Chest chest;

    private Block block;

    public AnimationGui(AddictzoneChests plugin, Player player, Chest chest) {
        super(3, chest.getPrefix()+" §bKiste", Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));

        this.plugin = plugin;
        this.weightedItemList = chest.getWeightedChestItemList();
        this.chest = chest;
        this.block = null;

        this.disableAllInteractions();
        this.getFiller().fillBorder(ItemPreset.fillItem(inventoryClickEvent -> {}));
        this.setItem(22, ItemPreset.closeItem(inventoryClickEvent -> this.close(player)));

        this.setItem(4, ItemBuilder.from(Material.HOPPER).name(Component.text("§aDein Gewinn!")).asGuiItem());

        this.setCloseGuiAction(inventoryCloseEvent -> {
            if(scheduledFuture != null && !scheduledFuture.isCancelled()){

                scheduledFuture.cancel(true);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    Bukkit.getServer().getPluginManager().callEvent(new ChestRewardEvent(true, player, chest, weightedItemList.next()));
                });

            }
        });


    }

    public AnimationGui(AddictzoneChests plugin, Player player, Chest chest, Block block) {
        super(3, chest.getPrefix()+" §bKiste", Set.of(InteractionModifier.PREVENT_ITEM_SWAP, InteractionModifier.PREVENT_ITEM_TAKE, InteractionModifier.PREVENT_ITEM_PLACE));

        this.plugin = plugin;
        this.weightedItemList = chest.getWeightedChestItemList();
        this.chest = chest;
        this.block = block;

        this.disableAllInteractions();
        this.getFiller().fillBorder(ItemPreset.fillItem(inventoryClickEvent -> {}));
        this.setItem(22, ItemPreset.closeItem(inventoryClickEvent -> this.close(player)));

        this.setItem(4, ItemBuilder.from(Material.HOPPER).name(Component.text("§aDein Gewinn!")).asGuiItem());

        this.setCloseGuiAction(inventoryCloseEvent -> {
            if(scheduledFuture != null && !scheduledFuture.isCancelled()){

                scheduledFuture.cancel(true);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    Bukkit.getServer().getPluginManager().callEvent(new ChestRewardEvent(true, player, chest, weightedItemList.next()));
                });

            }
        });


    }

    @Override
    public void open(@NotNull HumanEntity player) {
        super.open(player);

        playAnimation((Player) player);
    }

    private void playAnimation(Player player){

        AtomicInteger round = new AtomicInteger();

        int[] slots = new int[]{16,15,14,13,12,11,10};

        this.scheduledFuture = plugin.getExecutorService().scheduleAtFixedRate(new AnimationTask(this, round, player, chest), 0, 50, TimeUnit.MILLISECONDS);

    }

    private void changeInterval(Player player, long period, TimeUnit timeUnit, AtomicInteger round, int[] slots){

        if(scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
            this.scheduledFuture = plugin.getExecutorService().scheduleAtFixedRate(new AnimationTask(this, round, player, chest), period, period, timeUnit);
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class AnimationTask implements Runnable{

        private Gui gui;
        private AtomicInteger round;
        private final int[] slots = new int[]{16,15,14,13,12,11,10};
        private Player player;
        private Chest chest;

        @Override
        public void run() {

            ChestItem chestItem = weightedItemList.next();

            if(weightedItemList.getElementWeightList().size() > 1 && gui.getGuiItem(slots[0]) != null) {
                while (chestItem.getId().toString().equals(new ChestItem().fromDocument(Document.parse(new NBTItem(gui.getGuiItem(slots[0]).getItemStack()).getString("chestItem"))).getId().toString())) {
                    chestItem = weightedItemList.next();
                }
            }

            ItemStack itemStack = ItemBuilder
                    .from(chestItem.getItemStack())
                    .build();

            ItemMeta itemMeta = itemStack.getItemMeta();

            List<Component> lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();
            lore.add(Component.text(" "));
            lore.add(Component.text("§7➥ Seltenheit: "+ ChatColor.translateAlternateColorCodes('&', chestItem.getItemRarity().getPrefix())));
            lore.add(Component.text("§7➥ Type: §b"+(new VoucherService().isVoucher(chestItem.getItemStack()) ? "Gutschein" : "Item")));

            itemMeta.lore(lore);

            itemStack.setItemMeta(itemMeta);

            NBTItem nbtItem = new NBTItem(itemStack);

            nbtItem.setString("chestItem", chestItem.toDocument().toJson());



            for(int i = 11; i != 17; i++) {
                moveItem(i);
            }

            if(gui.getGuiItem(slots[0]) != null) {
                gui.updateItem(slots[0], ItemBuilder.from(nbtItem.getItem()).asGuiItem());
            }else{
                gui.setItem(slots[0], ItemBuilder.from(nbtItem.getItem()).asGuiItem());
            }

            gui.update();

            round.getAndIncrement();

            if(round.get() == 10){
                changeInterval(player, 100, TimeUnit.MILLISECONDS, round, slots);
            }else if(round.get() == 20){
                changeInterval(player,200, TimeUnit.MILLISECONDS, round, slots);
            }else if(round.get() == 25){
                changeInterval(player,300, TimeUnit.MILLISECONDS, round, slots);
            }else if(round.get() == 30){
                changeInterval(player,450, TimeUnit.MILLISECONDS, round, slots);
            }else if(round.get() == 32){
                changeInterval(player,600, TimeUnit.MILLISECONDS, round, slots);
            }else if(round.get() == 35){
                scheduledFuture.cancel(true);

                if(block != null){
                    gui.updateItem(18, ItemBuilder.from(Material.ARROW).name(Component.text("§9Zurück")).asGuiItem(inventoryClickEvent -> {
                        new ChestPreviewGui(plugin, player, chest, block).open(player);
                    }));
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    Bukkit.getServer().getPluginManager().callEvent(new ChestRewardEvent(true, player, chest, new ChestItem().fromDocument(Document.parse(new NBTItem(gui.getGuiItem(13).getItemStack()).getString("chestItem")))));
                });
            }
        }

        private void moveItem(int slot){
                if (gui.getGuiItem(slot) != null) {
                    if (gui.getGuiItem(slot-1) != null) {
                        gui.updateItem(slot-1, gui.getGuiItem(slot));
                    } else {
                        gui.setItem(slot-1, gui.getGuiItem(slot));
                    }
                }

        }
    }

}
