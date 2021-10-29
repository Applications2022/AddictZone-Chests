package de.ruben.addictzonechests.command;

import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.gui.EditChestGui;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.RarityService;
import de.ruben.xdevapi.XDevApi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KistenCommand implements CommandExecutor {

    private AddictzoneChests plugin;
    private RarityService rarityService;
    private ChestService chestService;

    public KistenCommand(AddictzoneChests plugin) {
        this.plugin = plugin;
        this.rarityService = new RarityService(plugin);
        this.chestService = new ChestService(plugin);
        plugin.getCommand("kisten").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) sender;

        if(!player.hasPermission("addictzone.chests.admin")){
            player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("noperm"));
            return true;
        }

        if(args.length == 2){

            if(args[0].equalsIgnoreCase("deleterarity")){
                String name = args[1].replace("-", " ");
                RarityService rarityService = new RarityService(plugin);

                if(!rarityService.existItemRarity(name)){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte gebe eine exestierende Seltenheit an!");
                    return true;
                }

                rarityService.deleteItemRarityAndFormatItems(name);
                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast die Seltenheit §b"+name+" §7erfolgreich gelöscht!");
            }else if(args[0].equalsIgnoreCase("editChest")){
                String name = args[1].replace("-", " ");

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Lade Chestinhalt....");

                XDevApi.getInstance().getxScheduler().async(() -> {
                    if(!chestService.existChest(name)){
                        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Diese Kiste exestiert nicht!");
                    }else {
                        Chest chest = chestService.getChest(name);

                        Bukkit.getScheduler().runTask(plugin, () -> new EditChestGui(plugin, player, chest).open(player));

                    }
                });

            }else{
                System.out.println(args[0]+" hi");
                sendHelpMessage(player);
            }

        }else if(args.length == 4){

            if(args[0].equalsIgnoreCase("createrarity")){
                String name = args[1].replace("-", " ");
                String prefix = args[2];

                if(!isInteger(args[3])){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte gebe einen Integer als weight an!");
                    return true;
                }

                Integer weight = Integer.parseInt(args[3]);

                new RarityService(plugin).createItemRarity(name, prefix, "", weight);

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast erfolgreich die Seltenheit §b"+name+" §7mit der Weight §b"+weight+" §7erstellt!");
            }else{
                sendHelpMessage(player);
            }

        }else{
            sendHelpMessage(player);
        }

        // /kisten createRarity <name> <prefix> <weight>

        return false;
    }

    private void sendHelpMessage(Player player){
        player.sendMessage("§7§m-------------------------------------------------");
        player.sendMessage(" ");
        player.sendMessage("§7Benutze: §b/kisten createRarity <name> <prefix> <weight>");
        player.sendMessage("§7Benutze: §b/kisten deleteRarity <name>");
        player.sendMessage("§7Benutze: §b/kisten editChest <name>");
        player.sendMessage(" ");
        player.sendMessage("§7§m-------------------------------------------------");
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
}
