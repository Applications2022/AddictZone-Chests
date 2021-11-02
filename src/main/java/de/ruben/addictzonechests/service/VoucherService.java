package de.ruben.addictzonechests.service;

import de.ruben.addictzonechests.model.vouchers.*;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class VoucherService {

    public boolean isVoucher(ItemStack itemStack){
        return new NBTItem(itemStack).hasKey("voucher");
    }

    public Voucher getVoucher(ItemStack itemStack){

        VoucherType voucherType = VoucherType.valueOf(new NBTItem(itemStack).getString("type"));

        switch (voucherType) {
            case CHEST:
                return new ChestVoucher().fromItemStack(itemStack);
            case COMMAND:
                return new CommandVoucher().fromItemStack(itemStack);
            case MONEY:
                return new MoneyVoucher().fromItemStack(itemStack);
            case LEVEL_PERMISSION:
                return new LevelPermissionVoucher().fromItemStack(itemStack);
            case PERMISSION:
                return new PermissionVoucher().fromItemStack(itemStack);
            default:
                return null;
        }
    }

    public ItemStack setChestVoucher(ItemStack itemStack, String chest, int amount){
        return new ChestVoucher(UUID.randomUUID(), chest, amount).toItemStack(itemStack);
    }

    public ItemStack setCommandVoucher(ItemStack itemStack, String command, boolean console){
        return new CommandVoucher(UUID.randomUUID(), command, console).toItemStack(itemStack);
    }

    public ItemStack setMoneyVoucher(ItemStack itemStack, double money){
        return new MoneyVoucher(UUID.randomUUID(), money).toItemStack(itemStack);
    }

    public ItemStack setLevelPermissionVoucher(ItemStack itemStack, String permissionPrefix, int increase, int maxLevel, int compensation){
        return new LevelPermissionVoucher(UUID.randomUUID(), permissionPrefix, increase, maxLevel, compensation).toItemStack(itemStack);
    }

    public ItemStack setPermissionVoucher(ItemStack itemStack, String permission, int compensation){
        return new PermissionVoucher(UUID.randomUUID(), permission, compensation).toItemStack(itemStack);
    }


}
