package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Speleologist extends Superpower {

    HashMap<Material, Material> instantSmelt = new HashMap<>();
    HashSet<Material> toDouble = new HashSet<>();

    public Speleologist(PowersHandler powersHandler) {
        super(powersHandler);
        instantSmelt.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        instantSmelt.put(Material.IRON_ORE, Material.IRON_INGOT);
        toDouble.add(Material.DIAMOND);
        toDouble.add(Material.EMERALD);
        toDouble.add(Material.COAL);
        toDouble.add(Material.LAPIS_LAZULI);
        toDouble.add(Material.REDSTONE);
    }

    @EventHandler
    public void onMine(BlockBreakEvent e) {
        if (powersHandler.getPower(e.getPlayer()) == Power.Speleologist) {
            e.setDropItems(false);
            Collection<ItemStack> drops = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer());
            for (ItemStack item : drops) {
                Material smelted = instantSmelt.get(item.getType());
                if (smelted != null) {
                    item.setType(smelted);
                    item.setAmount(item.getAmount() * 2);
                }
                if (toDouble.contains(item.getType())) {
                    item.setAmount(item.getAmount() * 2);
                }
                Location dropLoc = e.getBlock().getLocation();
                World world = e.getPlayer().getWorld();
                world.dropItemNaturally(dropLoc, item);
            }
        }
    }

}
