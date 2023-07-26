package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.InstantBreakData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InstantBreak extends SkillImplementation {
    public InstantBreak(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Superhero hero = getPowersHandler().getSuperhero(e.getPlayer());
            Collection<SkillData> skillDatas = hero.getSkillData(Skill.getSkill("INSTANTBREAK"));
            for (SkillData skillData : skillDatas) {
                InstantBreakData instantBreakData = (InstantBreakData) skillData;
                Player player = e.getPlayer();
                Block block = e.getClickedBlock();
                ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                ItemStack toBreakWith = new ItemStack(instantBreakData.getBreakUsing());
                toBreakWith.addUnsafeEnchantments(item.getEnchantments());
                if (instantBreakData.canBreak(block.getType())) {
                    if (!skillData.areConditionsTrue(player, block.getLocation())) {
                        return;
                    }
                    Collection<ItemStack> itemStackDrops = block.getDrops(toBreakWith);
                    final List<Item> entityDrops = new ArrayList<>();
                    World world = block.getWorld();
                    itemStackDrops.forEach((itemStack) -> entityDrops.add(world.dropItemNaturally(block.getLocation(), itemStack)));
                    BlockDropItemEvent blockDropItemEvent = new BlockDropItemEvent(block, block.getState(), e.getPlayer(), entityDrops);
                    Bukkit.getPluginManager().callEvent(blockDropItemEvent);
                    if (blockDropItemEvent.isCancelled()) {
                        for (Item drop : blockDropItemEvent.getItems()) {
                            drop.remove();
                        }
                    }
                    else {
                        int experience = calculateExperience(block.getType());
                        if (experience > 0 && !item.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                            ExperienceOrb spawn = world.spawn(block.getLocation(), ExperienceOrb.class);
                            spawn.setExperience(experience);
                        }
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public int calculateExperience(Material type) {
        return switch (type) {
            case COAL_ORE, NETHER_GOLD_ORE -> 1;
            case GOLD_ORE, DIAMOND_ORE -> 5;
            case LAPIS_LAZULI, NETHER_QUARTZ_ORE, REDSTONE_ORE -> 3;
            case SPAWNER -> 29;
            default -> 0;
        };
    }
}
