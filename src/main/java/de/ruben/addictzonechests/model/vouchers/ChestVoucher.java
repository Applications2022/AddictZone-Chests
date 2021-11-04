package de.ruben.addictzonechests.model.vouchers;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.KeyService;
import de.ruben.xdevapi.XDevApi;
import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChestVoucher implements Voucher{

    private UUID uuid;
    private String chest;
    private Integer amount;

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public VoucherType getVoucherType() {
        return VoucherType.CHEST;
    }

    @Override
    public ItemStack toItemStack(ItemStack itemStack) {

        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setBoolean("voucher", true);
        nbtItem.setUUID("id", uuid);
        nbtItem.setString("type", getVoucherType().name());
        nbtItem.setString("chest", chest);
        nbtItem.setInteger("amount", amount);

        itemStack = nbtItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<Component> lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();

        lore.add(Component.text(" "));
        lore.add(Component.text("§7➥ Du erhältst §b"+amount+" "+new ChestService(AddictzoneChests.getInstance()).getChest(chest).getPrefix() +" §aKiste§7(§an§7)."));

        itemMeta.lore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public Voucher fromItemStack(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        this.uuid = nbtItem.getUUID("id");
        this.chest = nbtItem.getString("chest");
        this.amount = nbtItem.getInteger("amount");

        return this;
    }

    @Override
    public void onWin(Player player, ItemStack itemStack) {

        new KeyService(AddictzoneChests.getInstance()).addKey(player.getUniqueId(), chest, amount);

        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast §b"
                +(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().toLowerCase())+
                " §7gewonnen!");

    }
}
