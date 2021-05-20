package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.InstantBreakData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class InstantBreak extends SkillImplementation {
    public InstantBreak(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Superhero hero = getPowersHandler().getSuperhero(e.getPlayer());
            Collection<SkillData> skillDatas = hero.getSkillData(Skill.INSTANTBREAK);
            for (SkillData skillData : skillDatas) {
                InstantBreakData instantBreakData = (InstantBreakData) skillData;
                Block block = e.getClickedBlock();
                ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                ItemStack toBreakWith = new ItemStack(instantBreakData.getBreakUsing());
                item.getEnchantments().forEach(toBreakWith::addEnchantment);
                if (instantBreakData.canBreak(block.getType())) {
                    BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, e.getPlayer());
                    Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
                    if (!blockBreakEvent.isCancelled()) {
                        if (blockBreakEvent.isDropItems()) {
                            block.breakNaturally(toBreakWith);
                        }
                        else {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}
