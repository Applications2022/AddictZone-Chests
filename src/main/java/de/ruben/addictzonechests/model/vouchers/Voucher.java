package de.ruben.addictzonechests.model.vouchers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface Voucher {

    UUID getUUID();
    VoucherType getVoucherType();
    ItemStack toItemStack(ItemStack itemStack);
    Voucher fromItemStack(ItemStack itemStack);
    void onWin(Player player, ItemStack itemStack);

}
