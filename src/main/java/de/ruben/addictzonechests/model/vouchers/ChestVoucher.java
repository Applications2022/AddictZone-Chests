package de.ruben.addictzonechests.model.vouchers;

import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        return nbtItem.getItem();
    }

    @Override
    public Voucher fromItemStack(ItemStack itemStack) {
        return null;
    }

    @Override
    public void onWin(Player player, ItemStack itemStack) {

        // TODO: KeyManager!

    }
}
