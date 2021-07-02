package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.events.HeroBlockBreakEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.InstantBreakData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
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
            Collection<SkillData> skillDatas = hero.getSkillData(Skill.getSkill("INSTANTBREAK"));
            for (SkillData skillData : skillDatas) {
                InstantBreakData instantBreakData = (InstantBreakData) skillData;
                Player player = e.getPlayer();
                Block block = e.getClickedBlock();
                ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                ItemStack toBreakWith = new ItemStack(instantBreakData.getBreakUsing());
                toBreakWith.addUnsafeEnchantments(item.getEnchantments());
                if (instantBreakData.canBreak(block.getType())) {
                    if (!skillData.areConditionsTrue(player, block)) {
                        return;
                    }
                    Collection<ItemStack> drops = block.getDrops(toBreakWith);
                    HeroBlockBreakEvent blockBreakEvent = new HeroBlockBreakEvent(block, e.getPlayer(), drops);
                    blockBreakEvent.callEvent();
                    drops = blockBreakEvent.getDrops();
                    if (!blockBreakEvent.isCancelled() && blockBreakEvent.isDropItems()) {
                        World world = e.getPlayer().getWorld();
                        if (blockBreakEvent.isDropItems()) {
                            for (ItemStack drop : drops) {
                                world.dropItemNaturally(block.getLocation(), drop);
                            }
                        }
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
        switch (type) {
            case COAL_ORE: //drop down
            case NETHER_GOLD_ORE: return 1;
            case GOLD_ORE: //drop down
            case DIAMOND_ORE: return 5;
            case LAPIS_LAZULI: //drop down
            case NETHER_QUARTZ_ORE: return 3;
            case REDSTONE_ORE: return 3;
            case SPAWNER: return 29;
            default: return 0;
        }
    }
}
