package de.ruben.addictzonechests.model.vouchers;

import de.ruben.addictzonechests.AddictzoneChests;
import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommandVoucher implements Voucher{
    private UUID uuid;
    private String command;
    private boolean console;


    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public VoucherType getVoucherType() {
        return VoucherType.COMMAND;
    }

    @Override
    public ItemStack toItemStack(ItemStack itemStack) {

        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setBoolean("voucher", true);
        nbtItem.setUUID("id", uuid);
        nbtItem.setString("type", getVoucherType().name());
        nbtItem.setString("command", command);
        nbtItem.setBoolean("console", console);

        return nbtItem.getItem();
    }

    @Override
    public Voucher fromItemStack(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        this.uuid = nbtItem.getUUID("id");
        this.command = nbtItem.getString("command");
        this.console = nbtItem.getBoolean("console");

        return this;
    }

    @Override
    public void onWin(Player player, ItemStack itemStack) {
        command = command.replace("%s", player.getName());
        command = command.startsWith("/") ? command.substring(1) : command;

        Bukkit.getScheduler().runTask(AddictzoneChests.getInstance(), () -> {
            if(console){
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }else{
                Bukkit.dispatchCommand(player, command);
            }
        });
    }
}
