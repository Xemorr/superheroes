package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PotionEffectPowers extends BukkitRunnable {

    PowersHandler powersHandler;

    public PotionEffectPowers(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Power power = powersHandler.getPower(player);
            List<PotionEffect> potionEffects = new ArrayList<>();
            if (power == null) {
                continue;
            }
            switch (power) {
                case Superhuman:
                    potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 60, 0));
                    potionEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0));
                    potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0));
                    potionEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 0));
                    potionEffects.add(new PotionEffect(PotionEffectType.JUMP, 60, 1));
                    break;
                case Strongman:
                    potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1));
                    break;
                case Speedster:
                    potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 60, 3));
                    break;
                case Chicken:
                    potionEffects.add(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0));
                    potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 60, 0));
                    break;
                case Mole:
                    potionEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 260, 0));
                    break;
                case LavaWalker:
                case Pyromaniac:
                    potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0));
                    break;
            }
            player.addPotionEffects(potionEffects);
        }
    }

}
