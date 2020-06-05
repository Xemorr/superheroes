package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

public class KingMidas extends Superpower {

    HashSet<Material> ingotBlocks = new HashSet<>();

    public KingMidas(PowersHandler powersHandler) {
        super(powersHandler);
        ingotBlocks.addAll(Arrays.asList(new Material[]{Material.IRON_BLOCK, Material.COAL_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK}));
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (powersHandler.getPower(player) == Power.KingMidas) {
                if (e.getItem().getItemStack().getType() == Material.APPLE) {
                    if (Math.random() > 0.95) {
                        e.getItem().getItemStack().setType(Material.ENCHANTED_GOLDEN_APPLE);
                    }
                    else {
                        e.getItem().getItemStack().setType(Material.GOLDEN_APPLE);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) != Power.KingMidas) {
                return;
            }
            Block block = e.getClickedBlock();
            if (ingotBlocks.contains(block.getType())) {
                block.setType(Material.GOLD_BLOCK);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            if (powersHandler.getPower(player) == Power.KingMidas) {
                World world = player.getWorld();
                world.dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.GOLD_NUGGET));
            }
        }
    }

}
