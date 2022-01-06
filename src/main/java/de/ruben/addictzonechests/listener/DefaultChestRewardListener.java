package de.ruben.addictzonechests.listener;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.event.ChestRewardEvent;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.service.ChestHistoryService;
import de.ruben.addictzonechests.service.VoucherService;
import de.ruben.xdevapi.XDevApi;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public record DefaultChestRewardListener(AddictzoneChests plugin) implements Listener {

    @EventHandler
    public void onChestRewardEvent(ChestRewardEvent event) {
        Player player = event.getPlayer();
        ChestItem chestItem = event.getWin();
        ItemStack itemStack = chestItem.getItemStack();

        if (new VoucherService().isVoucher(chestItem.getItemStack())) {
            new VoucherService().getVoucher(chestItem.getItemStack()).onWin(player, chestItem.getItemStack());

            if(chestItem.getItemRarity().getBroadcast()){

                Bukkit.broadcastMessage("§8§m--------------------------------------------------");
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+ ChatColor.translateAlternateColorCodes('&', chestItem.getItemRarity().getPrefix()));
                Bukkit.broadcastMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7"+(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name()));
                Bukkit.broadcastMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§b"+player.getName());
                Bukkit.broadcastMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+event.getChest().getPrefix()+" §aKiste");
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage("§8§m--------------------------------------------------");

                player.sendTitle(ChatColor.translateAlternateColorCodes('&', chestItem.getItemRarity().getPrefix()),"§6§lHerzlichen Glückwunsch!");


                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f);
                    player.closeInventory();
                });
            }

        } else {

            if(chestItem.getItemRarity().getBroadcast()){

                Bukkit.broadcastMessage("§8§m--------------------------------------------------");
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+ChatColor.translateAlternateColorCodes('&', chestItem.getItemRarity().getPrefix()));
                TextComponent textComponent = new TextComponent(XDevApi.getInstance().getMessageService().getMessage("prefix"));
                textComponent.addExtra(getTextComponent(chestItem.getItemStack()));
                Bukkit.broadcast(textComponent);
                Bukkit.broadcastMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§b"+player.getName());
                Bukkit.broadcastMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+event.getChest().getPrefix()+" §aKiste");
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage("§8§m--------------------------------------------------");

                player.sendTitle(ChatColor.translateAlternateColorCodes('&', chestItem.getItemRarity().getPrefix()),"§6§lHerzlichen Glückwunsch!");

                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f);
                    player.closeInventory();
                });
            }else{

                TextComponent textComponent = new TextComponent(XDevApi.getInstance().getMessageService().getMessage("prefix")+"Du hast ");
                textComponent.addExtra(getTextComponent(chestItem.getItemStack()));
                textComponent.addExtra(" ");
                textComponent.addExtra("§7gewonnen!");

                player.sendMessage(textComponent);

            }

            player.getInventory().addItem(chestItem.getItemStack());
        }

        XDevApi.getInstance().getxScheduler().async(() -> {
            new ChestHistoryService(plugin).addHistory(player.getUniqueId(), event.getChest().getPrefix(), new Date(System.currentTimeMillis()), chestItem);
        });

    }


    private String convertItemStackToJson(ItemStack itemStack) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }

    private TextComponent getTextComponent(ItemStack itemStack){

        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new net.md_5.bungee.api.chat.TextComponent(convertItemStackToJson(itemStack))
        };
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);
        TextComponent component = new TextComponent((itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name()));
        component.setHoverEvent(event);

        return component;

    }
}
