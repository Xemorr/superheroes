package me.xemor.superheroes.events;

import org.bukkit.Bukkit;
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
    private boolean changed;

    public HeroBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player, @NotNull Collection<ItemStack> drops) {
        super(theBlock, player);
        this.drops = drops;
        changed = false;
    }

    public boolean isDropsChanged() {
        return changed;
    }

    public Collection<ItemStack> getDrops() {
        return drops;
    }

    public void setDrops(Collection<ItemStack> drops) {
        this.drops = drops;
        changed = true;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }


}
