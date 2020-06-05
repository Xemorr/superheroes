package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Chicken extends BukkitRunnable {

    PowersHandler powersHandler;

    public Chicken(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (powersHandler.getPower(player) == Power.Chicken) {
                Random random = new Random();
                if (1 == random.nextInt(5)) {
                    World world = player.getWorld();
                    world.dropItemNaturally(player.getLocation(), new ItemStack(Material.EGG));
                    world.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1F, 1F);
                }
            }
        }
    }
}
