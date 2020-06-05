package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

public class Mole extends Superpower {

    HashSet<Material> blocksToBreak = new HashSet<>();

    public Mole(PowersHandler powersHandler) {
        super(powersHandler);
        blocksToBreak.addAll(Arrays.asList(new Material[]{Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT, Material.STONE, Material.COBBLESTONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE}));
    }

    @EventHandler
    public void onBlockHit(PlayerInteractEvent e) {
        if (getPowersHandler().getPower(e.getPlayer()) != Power.Mole) {
            return;
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (blocksToBreak.contains(block.getType())) {
                block.breakNaturally(new ItemStack(Material.DIAMOND_PICKAXE));
            }
        }
    }



}
