package me.xemor.superheroes2.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class HeroBlockBreakEvent extends BlockBreakEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private Collection<ItemStack> drops;

    public HeroBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player, @NotNull Collection<ItemStack> drops) {
        super(theBlock, player);
        this.drops = drops;
    }

    public Collection<ItemStack> getDrops() {
        return drops;
    }

    public void callEvent() {
        Bukkit.getServer().getPluginManager().callEvent(this);
        if (!this.isCancelled()) {
            block.setType(Material.AIR);
            if (this.isDropItems()) {
                for (ItemStack drop : drops) {
                    getPlayer().getWorld().dropItemNaturally(getBlock().getLocation(), drop);
                }
            }
        }
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


}
