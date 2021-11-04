package de.ruben.addictzonechests.animation;

import com.google.common.util.concurrent.AtomicDouble;
import de.ruben.addictzonechests.AddictzoneChests;
import de.ruben.xdevapi.XDevApi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArmorstandAnimation {

    private AddictzoneChests plugin;
    private ScheduledFuture<?> scheduledFuture;
    private ArmorStand armorStand;
    private Location chestLocation;
    private double speed;

    public void animate(){

        AtomicDouble rounds = new AtomicDouble((2/speed));

        Location spawnLocation = chestLocation;
        spawnLocation.setY(chestLocation.getBlockY()+1);

        Bukkit.getScheduler().runTask(plugin, () -> {
            System.out.println("sync Task!");
            this.armorStand = (ArmorStand) chestLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setHelmet(new ItemStack(Material.ENDER_CHEST, 1));
            armorStand.setBasePlate(false);
            armorStand.setGravity(false);

        });

        this.scheduledFuture = plugin.getExecutorService().scheduleAtFixedRate(() -> {


            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                Location newLoc = armorStand.getLocation();
                newLoc.setY(newLoc.getY() + speed);

                return armorStand.teleport(newLoc);
            });

            rounds.getAndSet(rounds.get()-1);

            if(rounds.get() < 1){
                scheduledFuture.cancel(true);
                armorStand.remove();
                System.out.println("yessss");
            }

            System.out.println("double");
            System.out.println(rounds.get());

        }, 0, 20, TimeUnit.MILLISECONDS);

    }




}
