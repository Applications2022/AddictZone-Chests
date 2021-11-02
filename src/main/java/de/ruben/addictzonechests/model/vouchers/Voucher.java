package de.ruben.addictzonechests.model.vouchers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface Voucher {

    public UUID getUUID();
    public VoucherType getVoucherType();
    public ItemStack toItemStack(ItemStack itemStack);
    public Voucher fromItemStack(ItemStack itemStack);
    public void onWin(Player player, ItemStack itemStack);

}
