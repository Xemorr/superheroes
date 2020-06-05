package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MoleRunnable extends BukkitRunnable {

    private PowersHandler powersHandler;
    public MoleRunnable(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }


    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (powersHandler.getPower(player) == Power.Mole) {
                World world = player.getWorld();
                if (world.getBlockAt(player.getLocation()).getLightLevel() > 10) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 160, 0));
                }
            }
        }
    }
}
