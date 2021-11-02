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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LevelPermissionVoucher implements Voucher{

    private UUID uuid;
    private String permissionPrefix;
    private Integer levelIncrease;
    private Integer maxLevel;
    private Integer compensation;

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public VoucherType getVoucherType() {
        return VoucherType.LEVEL_PERMISSION;
    }

    @Override
    public ItemStack toItemStack(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setBoolean("voucher", true);
        nbtItem.setUUID("id", uuid);
        nbtItem.setString("type", getVoucherType().name());
        nbtItem.setString("permissionPrefix", permissionPrefix);
        nbtItem.setInteger("levelIncrease", levelIncrease);
        nbtItem.setInteger("maxLevel", maxLevel);
        nbtItem.setInteger("compensation", compensation);

        return nbtItem.getItem();
    }

    @Override
    public Voucher fromItemStack(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);

        this.uuid = nbtItem.getUUID("id");
        this.permissionPrefix = nbtItem.getString("permissionPrefix");
        this.levelIncrease = nbtItem.getInteger("levelIncrease");
        this.maxLevel = nbtItem.getInteger("maxLevel");
        this.compensation = nbtItem.getInteger("compensation");

        return this;
    }

    @Override
    public void onWin(Player player, ItemStack itemStack) {

        XDevApi.getInstance().getxScheduler().async(() -> {

            if(getHighestPermission(player) >= maxLevel){
                new CashService().addValue(player.getUniqueId(), compensation, cashAccount -> {
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du kannst nicht nochmal §b"
                            +(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().toLowerCase())+
                            " §7gewinnen, deswegen hast du §b"+XDevApi.getInstance().getxUtil().getStringUtil().moneyFormat(compensation)+"€ §7erstattet bekommen!");
                });
            }else{
                User user = AddictzoneChests.getInstance().getLuckperms().getPlayerAdapter(Player.class).getUser(player);
                user.data().add(PermissionNode.builder(permissionPrefix+(getHighestPermission(player)+levelIncrease)).build());
                AddictzoneChests.getInstance().getLuckperms().getUserManager().saveUser(user);

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast §b"
                        +(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().toLowerCase())+
                        " §7gewonnen!");
            }

        });

    }

    private int getHighestPermission(Player player){
        for(int i = maxLevel+5; i > 0; i--){
            if(player.hasPermission(permissionPrefix+i)){
                return i;
            }
        }

        return 0;
    }
}
