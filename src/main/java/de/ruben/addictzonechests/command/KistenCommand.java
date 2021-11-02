package de.ruben.addictzonechests.command;

import com.google.gson.Gson;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.addictzonechests.gui.AnimationGui;
import de.ruben.addictzonechests.gui.ChestHistoryGui;
import de.ruben.addictzonechests.gui.EditChestGui;
import de.ruben.addictzonechests.gui.EditGui;
import de.ruben.addictzonechests.model.chest.Chest;
import de.ruben.addictzonechests.model.chest.ChestItem;
import de.ruben.addictzonechests.model.chest.ChestLocation;
import de.ruben.addictzonechests.service.ChestLocationService;
import de.ruben.addictzonechests.service.ChestService;
import de.ruben.addictzonechests.service.KeyService;
import de.ruben.addictzonechests.service.RarityService;
import de.ruben.xdevapi.XDevApi;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

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

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("edit")){
                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Lade Chests....");

                XDevApi.getInstance().getxScheduler().async(() -> {
                    List<Chest> chests = chestService.getChests();

                    Bukkit.getScheduler().runTask(plugin, () -> new EditGui(player, plugin, chests).open(player));
                });
            }else if(args[0].equalsIgnoreCase("removelocation")){

                Block block = player.getTargetBlock(null, 5);

                if(block == null || block.getType() == Material.AIR){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte schaue einen Block an!");
                    return true;
                }

                if(!block.hasMetadata("chestBlock")){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Auf diesen Block ist  keine Kiste gesetzt!");
                    return true;
                }

                ChestLocation chestLocation = (ChestLocation) block.getMetadata("chestBlock").get(0).value();

                new ChestLocationService(plugin).deleteChestLocation(chestLocation.getUuid());

                block.removeMetadata("chestBlock", plugin);

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Block erfolgreich entfernt!");

            }else{
                sendHelpMessage(player);
            }
        }else if(args.length == 2){

            if(args[0].equalsIgnoreCase("deleterarity")){
                String name = args[1].replace("-", " ");

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

            }else if(args[0].equalsIgnoreCase("testanimation")){
                String name = args[1].replace("-", " ");

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Lade Chest....");

                XDevApi.getInstance().getxScheduler().async(() -> {
                    if(!chestService.existChest(name)){
                        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Diese Kiste exestiert nicht!");
                    }else {
                        Chest chest = chestService.getChest(name);

                        Bukkit.getScheduler().runTask(plugin, () -> new AnimationGui(plugin, player, chest).open(player));

                    }
                });

            }else if(args[0].equalsIgnoreCase("setItem")){
                String rarity = args[1];

                if(!rarityService.existItemRarity(rarity)){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte gebe eine exestierende Seltenheit an!");
                    return true;
                }

                ItemStack itemStack = player.getInventory().getItemInMainHand();

                if(itemStack == null || itemStack.getType() == Material.AIR){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte nehme ein Item in die Hand!");
                    return true;
                }

                NBTItem nbtItem = new NBTItem(itemStack);
                nbtItem.setString("chestItem", new ChestItem(UUID.randomUUID(), itemStack.serialize(), rarity).toDocument().toJson());

                player.getInventory().setItemInMainHand(nbtItem.getItem());

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast das Item erfolgreich als Kisten-Item gesetzt!");

            }else if(args[0].equalsIgnoreCase("history")){
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                new ChestHistoryGui(plugin, player, offlinePlayer.getUniqueId(), ChestHistoryGui.SortType.DATUM_ASCENDING).open(player);

            }else if(args[0].equalsIgnoreCase("addlocation")){
                String chest = args[1];

                if(!chestService.existChest(chest)){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte gebe eine exestierende Kiste an!");
                    return true;
                }

                Block block = player.getTargetBlock(null, 5);

                if(block == null || block.getType() == Material.AIR){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte schaue einen Block an!");
                    return true;
                }

                if(block.hasMetadata("chestBlock")){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Auf diesen Block ist bereits eine Kiste gesetzt! Benutze §b/kisten deleteLocation §7um sie zu entfernen!");
                    return true;
                }

                ChestLocation chestLocation = new ChestLocation(UUID.randomUUID(), block.getLocation(), chest);

                new ChestLocationService(plugin).addChestLocation(chestLocation);

                block.setMetadata("chestBlock", new FixedMetadataValue(plugin, chestLocation));
                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast die angegebene Kiste erfolgreich auf den Block gesetzt!");

            }else{
                System.out.println(args[0]+" hi");
                sendHelpMessage(player);
            }

        }else if(args.length == 3){
            if(args[0].equalsIgnoreCase("createchest")){

                String chestName = args[1].replace("-", " ");
                String prefix = ChatColor.translateAlternateColorCodes('&', args[2]);

                if(chestService.existChest(chestName)){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Diese Kiste exestiert bereits!");
                    return true;
                }

                chestService.createChest(chestName, prefix);

                player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast die Kiste "+prefix+" §7erfolgreich erstellt!");

            }else {
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

        }else if(args.length == 5){
            if(args[0].equalsIgnoreCase("key")){
                String action = args[1];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);

                if(!chestService.existChest(args[3])){
                    player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Diese Kiste exestiert nicht!");
                    return true;
                }

                Chest chest = chestService.getChest(args[3]);
                Integer amount = Integer.valueOf(args[4]);

                switch (action.toLowerCase()){
                    case "add":
                        new KeyService(plugin).addKey(offlinePlayer.getUniqueId(), chest.getName(), amount);

                        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast dem Spieler §b"+offlinePlayer.getName()+" §7erfolgreich §b"+amount+"x "+chest.getPrefix()+" §7Kiste(n) §7hinzugefügt!");

                        if(offlinePlayer.isOnline()){
                            offlinePlayer.getPlayer().sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§b"+player.getName()+" §7hat dir §b"+amount+"x "+chest.getPrefix()+" §7Kiste(n) hinzugefügt!");
                        }
                        break;
                    case "set":
                        new KeyService(plugin).setKey(offlinePlayer.getUniqueId(), chest.getName(), amount);
                        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast die "+chest.getPrefix()+" §7Kisten von §b"+offlinePlayer.getName()+" §7erfolgreich auf §b"+amount+" §7gesetzt!");
                        if(offlinePlayer.isOnline()){
                            offlinePlayer.getPlayer().sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§b"+player.getName()+" §7hat deine §b"+chest.getPrefix()+" §7Kiste(n) auf §b"+amount+" §7gesetzt!");
                        }
                        break;
                    case "remove":
                        new KeyService(plugin).removeKey(offlinePlayer.getUniqueId(), chest.getName(), amount);
                        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Du hast dem Spieler §b"+offlinePlayer.getName()+" §7erfolgreich §b"+amount+"x "+chest.getPrefix()+" §7Kiste(n) §7entfernt!");
                        if(offlinePlayer.isOnline()){
                            offlinePlayer.getPlayer().sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§b"+player.getName()+" §7hat dir §b"+amount+"x "+chest.getPrefix()+" §7Kiste(n) entfernt!");
                        }
                        break;
                    default:
                        player.sendMessage(XDevApi.getInstance().getMessageService().getMessage("prefix")+"§7Bitte gebe §badd, set §7oder §bremove §7als Aktion an!");
                        break;
                }
            }else{
                sendHelpMessage(player);
            }
        }else{
            sendHelpMessage(player);
        }

        return false;
    }

    private void sendHelpMessage(Player player){
        player.sendMessage("§7§m-------------------------------------------------");
        player.sendMessage(" ");
        player.sendMessage("§7Benutze: §b/kisten createRarity <name> <prefix> <weight>");
        player.sendMessage("§7Benutze: §b/kisten key add|set|remove <name> <amount>");
        player.sendMessage("§7Benutze: §b/kisten createChest <name> <prefix>");
        player.sendMessage("§7Benutze: §b/kisten setItem <rarity>");
        player.sendMessage("§7Benutze: §b/kisten deleteRarity <name>");
        player.sendMessage("§7Benutze: §b/kisten editChest <name>");
        player.sendMessage("§7Benutze: §b/kisten history <name>");
        player.sendMessage("§7Benutze: §b/kisten addlocation <chest>");
        player.sendMessage("§7Benutze: §b/kisten testanimation <name>");
        player.sendMessage("§7Benutze: §b/kisten edit");
        player.sendMessage("§7Benutze: §b/kisten deletelocation");
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
