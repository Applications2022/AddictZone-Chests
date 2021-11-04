package de.ruben.addictzonechests.model.vouchers;

import de.ruben.xcore.currency.service.CashService;
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
public class MoneyVoucher implements Voucher{
    private UUID uuid;
    private Double money;


    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public VoucherType getVoucherType() {
        return VoucherType.MONEY;
    }

    @Override
    public ItemStack toItemStack(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setBoolean("voucher", true);
        nbtItem.setUUID("id", uuid);
        nbtItem.setString("type", getVoucherType().name());
        nbtItem.setDouble("money", money);

        itemStack = nbtItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<Component> lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();

        lore.add(Component.text(" "));
        lore.add(Component.text("§7➥ Du erhältst §b"+XDevApi.getInstance().getxUtil().getStringUtil().moneyFormat(money)+"€ §7auf dein §aKonto§7."));

        itemMeta.lore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public Voucher fromItemStack(ItemStack itemStack) {

        NBTItem nbtItem = new NBTItem(itemStack);

        this.uuid = nbtItem.getUUID("id");
        this.money = nbtItem.getDouble("money");

        return this;
    }

    @Override
    public void onWin(Player player, ItemStack itemStack) {
        new CashService().addValue(player.getUniqueId(), money, cashAccount -> player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"Du hast §b"+XDevApi.getInstance().getxUtil().getStringUtil().moneyFormat(money)+"€ §7erstattet bekommen!"));
    }
}
