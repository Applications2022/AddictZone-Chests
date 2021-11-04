package de.ruben.addictzonechests.model.vouchers;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.xcore.currency.service.CashService;
import de.ruben.xdevapi.XDevApi;
import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
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
public class RankVoucher implements Voucher{

    private UUID uuid;
    private String rank;
    private Integer compensation;

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public VoucherType getVoucherType() {
        return VoucherType.RANK;
    }

    @Override
    public ItemStack toItemStack(ItemStack itemStack) {

        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setBoolean("voucher", true);
        nbtItem.setUUID("id", uuid);
        nbtItem.setString("type", getVoucherType().name());
        nbtItem.setString("rank", rank);
        nbtItem.setInteger("compensation", compensation);

        itemStack = nbtItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<Component> lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();

        Group group = AddictzoneChests.getInstance().getLuckperms().getGroupManager().getGroup(rank);

        lore.add(Component.text(" "));
        lore.add(Component.text("§7➥ Du erhältst den §"+group.getCachedData().getMetaData().getMetaValue("color")+group.getCachedData().getMetaData().getPrefix()+" §bRang"));

        itemMeta.lore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public Voucher fromItemStack(ItemStack itemStack) {

        NBTItem nbtItem = new NBTItem(itemStack);

        this.uuid = nbtItem.getUUID("id");
        this.rank = nbtItem.getString("rank");
        this.compensation = nbtItem.getInteger("compensation");

        return this;
    }

    @Override
    public void onWin(Player player, ItemStack itemStack) {

        Group newGroup = AddictzoneChests.getInstance().getLuckperms().getGroupManager().getGroup(rank);
        Group playerGroup = AddictzoneChests.getInstance().getLuckperms().getGroupManager().getGroup(AddictzoneChests.getInstance().getLuckperms().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());

        if(newGroup.getWeight().getAsInt() > playerGroup.getWeight().getAsInt()){
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user "+player.getName()+" parent add "+rank);

            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast §b"
                    +(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().toLowerCase())+
                    " §7gewonnen!");
        }else{
            new CashService().addValue(player.getUniqueId(), compensation, cashAccount -> player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Da du den §b"
                    +(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().toLowerCase())+
                    " §7schon besitzt hast du §b"+XDevApi.getInstance().getxUtil().getStringUtil().moneyFormat(compensation)+"€ §7erstattet bekommen!"));
        }

    }
}
