package de.ruben.addictzonechests.model.vouchers;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.xcore.currency.service.CashService;
import de.ruben.xdevapi.XDevApi;
import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionVoucher implements Voucher{

    private UUID uuid;
    private String permission;
    private Integer compensation;


    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public VoucherType getVoucherType() {
        return VoucherType.PERMISSION;
    }

    @Override
    public ItemStack toItemStack(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setBoolean("voucher", true);
        nbtItem.setUUID("id", uuid);
        nbtItem.setString("type", getVoucherType().name());
        nbtItem.setString("permission", permission);
        nbtItem.setInteger("compensation", compensation);

        return nbtItem.getItem();
    }

    @Override
    public Voucher fromItemStack(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        this.uuid = nbtItem.getUUID("id");
        this.permission = nbtItem.getString("permission");
        this.compensation = nbtItem.getInteger("compensation");

        return this;
    }

    @Override
    public void onWin(Player player, ItemStack itemStack) {
        if(player.hasPermission(permission)){
            new CashService().addValue(player.getUniqueId(), compensation, cashAccount -> player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Da du §b"
                    +(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().toLowerCase())+
                    " §7schon besitzt hast du §b"+XDevApi.getInstance().getxUtil().getStringUtil().moneyFormat(compensation)+"€ §7erstattet bekommen!"));
        }else{
            User user = AddictzoneChests.getInstance().getLuckperms().getPlayerAdapter(Player.class).getUser(player);
            user.data().add(PermissionNode.builder(permission).build());
            AddictzoneChests.getInstance().getLuckperms().getUserManager().saveUser(user);

            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast §b"
                    +(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().toLowerCase())+
                    " §7gewonnen!");
        }
    }

}
