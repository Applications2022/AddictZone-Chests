package de.ruben.addictzonechests.command;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.service.VoucherService;
import de.ruben.xdevapi.XDevApi;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VoucherCommand implements CommandExecutor {
    private final VoucherService voucherService = new VoucherService();


    public VoucherCommand(AddictzoneChests plugin){
        plugin.getCommand("voucher").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command commander, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;

        if(!player.hasPermission("addictzone.chests.admin")){
            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("noperm"));
            return true;
        }

        if(args.length >= 4 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("command")){
            boolean console = Boolean.valueOf(args[2]);

            String command = "";

            for(int i = 3; i < args.length; i++){
                command += args[i]+" ";
            }

            player.getInventory().setItemInMainHand(
                    voucherService.setCommandVoucher(player.getInventory().getItemInMainHand(),command, console)
            );

            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt!");
        }else if(args.length == 3){
            if(args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("money")){
                Double money = Double.valueOf(args[2]);

                if (!itemSatckIsValid(player)) return true;

                player.getInventory().setItemInMainHand(
                        voucherService.setMoneyVoucher(player.getInventory().getItemInMainHand(), money)
                );

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt!");

            }else{
                sendHelpMessage(player);
            }
        }else if(args.length == 4){
            if(args[0].equalsIgnoreCase("create")) {
                if (args[1].equalsIgnoreCase("chest")) {
                    String chest = args[2];
                    Integer amount = Integer.valueOf(args[3]);

                    if (!itemSatckIsValid(player)) return true;

                    player.getInventory().setItemInMainHand(
                            voucherService.setChestVoucher(player.getInventory().getItemInMainHand(), chest, amount)
                    );

                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt!");
                } else if(args[1].equalsIgnoreCase("perm")){
                    String permission = args[2];
                    Integer compensation = Integer.valueOf(args[3]);

                    if (!itemSatckIsValid(player)) return true;

                    player.getInventory().setItemInMainHand(
                            voucherService.setPermissionVoucher(player.getInventory().getItemInMainHand(), permission, compensation)
                    );

                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt!");

                }else{
                    sendHelpMessage(player);
                }
            }else{
                sendHelpMessage(player);
            }
        }else if(args.length == 6){
            if(args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("levelperm")){

                if (!itemSatckIsValid(player)) return true;

                String permissionprefix = args[2];
                Integer levelIncrease = Integer.valueOf(args[3]);
                Integer maxLevel = Integer.valueOf(args[4]);
                Integer compensation = Integer.valueOf(args[5]);

                player.getInventory().setItemInMainHand(
                        voucherService.setLevelPermissionVoucher(player.getInventory().getItemInMainHand(), permissionprefix, levelIncrease, maxLevel, compensation)
                );

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt!");
            }else{
                sendHelpMessage(player);
            }
        }else{
            sendHelpMessage(player);
        }

        return false;
    }

    private boolean itemSatckIsValid(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if(itemStack == null || itemStack.getType() == Material.AIR){
            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte nehme ein Item in die Hand!");
            return false;
        }

        if(voucherService.isVoucher(itemStack)){
            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Dieses Item ist bereits ein Gutschein!");
            return false;
        }
        return true;
    }

    private void sendHelpMessage(Player player){
        player.sendMessage("§7§m-------------------------------------------------");
        player.sendMessage(" ");
        player.sendMessage("§7Benutze: §b/voucher create levelperm <permissionprefix> <increase> <maxlevel> <compensation>");
        player.sendMessage("§7Benutze: §b/voucher create perm <permission> <compensation>");
        player.sendMessage("§7Benutze: §b/voucher create command <console> <command>");
        player.sendMessage("§7Benutze: §b/voucher create chest <type> <amount>");
        player.sendMessage("§7Benutze: §b/voucher create money <money>");
        player.sendMessage(" ");
        player.sendMessage("§7§m-------------------------------------------------");
    }
}
