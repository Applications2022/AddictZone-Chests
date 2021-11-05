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
            boolean console = Boolean.parseBoolean(args[2]);

            StringBuilder command = new StringBuilder();

            for(int i = 3; i < args.length; i++){
                command.append(args[i]).append(" ");
            }

            player.getInventory().setItemInMainHand(
                    voucherService.setCommandVoucher(player.getInventory().getItemInMainHand(), command.toString(), console)
            );

            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt!");
        }else if(args.length == 3){
            if(args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("money")){
                double money = Double.parseDouble(args[2]);

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
                    int amount = Integer.parseInt(args[3]);

                    if (!itemSatckIsValid(player)) return true;

                    player.getInventory().setItemInMainHand(
                            voucherService.setChestVoucher(player.getInventory().getItemInMainHand(), chest, amount)
                    );

                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt!");
                } else if(args[1].equalsIgnoreCase("perm")){
                    String permission = args[2];
                    int compensation = Integer.parseInt(args[3]);

                    if (!itemSatckIsValid(player)) return true;

                    player.getInventory().setItemInMainHand(
                            voucherService.setPermissionVoucher(player.getInventory().getItemInMainHand(), permission, compensation)
                    );

                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt.");

                }else if(args[1].equalsIgnoreCase("rank")){
                    String rank = args[2];
                    int compensation = Integer.parseInt(args[3]);

                    if (!itemSatckIsValid(player)) return true;

                    player.getInventory().setItemInMainHand(
                            voucherService.setRankVoucher(player.getInventory().getItemInMainHand(), rank, compensation)
                    );

                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt.");

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
                int levelIncrease = Integer.parseInt(args[3]);
                int maxLevel = Integer.parseInt(args[4]);
                int compensation = Integer.parseInt(args[5]);

                player.getInventory().setItemInMainHand(
                        voucherService.setLevelPermissionVoucher(player.getInventory().getItemInMainHand(), permissionprefix, levelIncrease, maxLevel, compensation)
                );

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast den Gutschein erfolgreich auf das Item in deiner Hand gesetzt.");
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
            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte nehme ein Item in die Hand.");
            return false;
        }

        if(voucherService.isVoucher(itemStack)){
            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Dieses Item ist bereits ein Gutschein.");
            return false;
        }
        return true;
    }

    private void sendHelpMessage(Player player){
        player.sendMessage("§8§m--------------------------------------------------");
        player.sendMessage(" ");
        player.sendMessage("§7Benutze: §b/voucher create levelperm §7<§bpermissionprefix§7> <§blevel§7> <§bmaxlevel§7> <§bErstattung§7>");
        player.sendMessage("§7Benutze: §b/voucher create perm §7<§bpermission§7> <§bErstattung§7>");
        player.sendMessage("§7Benutze: §b/voucher create rank §7<§bRann§7> <§bErstattung§7>");
        player.sendMessage("§7Benutze: §b/voucher create command §7<[§cboolean§7]§bconsole§7> <§bcommand§7>");
        player.sendMessage("§7Benutze: §b/voucher create chest §7<§bKiste§7> <§bamount§7>");
        player.sendMessage("§7Benutze: §b/voucher create money §7<§bamount§7>");
        player.sendMessage(" ");
        player.sendMessage("§8§m--------------------------------------------------");
    }
}
