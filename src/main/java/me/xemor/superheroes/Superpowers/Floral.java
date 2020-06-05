package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Floral extends Superpower {

    public Floral(PowersHandler powersHandler) {
        super(powersHandler);
    }

    Material[] flowers = new Material[]{Material.POPPY, Material.DANDELION,
    Material.BLUE_ORCHID, Material.OXEYE_DAISY, Material.PINK_TULIP, Material.WHITE_TULIP, Material.RED_TULIP,
    Material.ORANGE_TULIP, Material.LILY_OF_THE_VALLEY, Material.AZURE_BLUET, Material.ALLIUM};

    @EventHandler
    public void onLookChange(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (powersHandler.getPower(player) != Power.Floral) {
            return;
        }
        World world = player.getWorld();
        Location eyeLocation = player.getEyeLocation();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLocation, eyeLocation.getDirection(), 20);
        if (rayTraceResult == null) {
            return;
        }
        Block block = rayTraceResult.getHitBlock();
        if (block.getType() == Material.GRASS) {
            block.setType(getRandomFlower());
            return;
        }
        if (block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT) {
            Block aboveBlock = block.getRelative(BlockFace.UP);
            if (aboveBlock.getType() == Material.AIR || aboveBlock.getType() == Material.GRASS) {
                aboveBlock.setType(getRandomFlower());
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) != Power.Floral) {
                return;
            }
            ItemStack item = e.getItem();
            if (item == null) {
                return;
            }
            List<PotionEffect> potionEffects = new ArrayList<>();
            switch (item.getType()) {
                case POPPY: potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 80, 0));break;
                case DANDELION: potionEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0)); break;
                case BLUE_ORCHID: potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 600, 0)); break;
                case OXEYE_DAISY: potionEffects.add(new PotionEffect(PotionEffectType.SLOW_FALLING, 300, 0)); break;
                case PINK_TULIP: potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 20, 2)); break;
                case WHITE_TULIP: player.getInventory().addItem(new ItemStack(Material.BONE_MEAL)); break;
                case RED_TULIP: potionEffects.add(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1200, 0)); break;
                case ORANGE_TULIP: potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0)); break;
                case LILY_OF_THE_VALLEY: potionEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0)); break;
                case AZURE_BLUET: potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 150, 2)); break;
                case ALLIUM: potionEffects.add(new PotionEffect(PotionEffectType.LEVITATION, 150, 1)); break;
                default: return;
            }
            player.setFoodLevel(player.getFoodLevel() + 1);
            player.addPotionEffects(potionEffects);
            if (item.getAmount() == 1) {
                player.getInventory().remove(item);
            }
            item.setAmount(item.getAmount() - 1);
        }
    }

    public Material getRandomFlower() {
        Random random = new Random();
        return flowers[random.nextInt(flowers.length)];
    }

}
