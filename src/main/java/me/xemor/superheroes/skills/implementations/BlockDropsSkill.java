package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.HeroBlockBreakEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.BlockDropsData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BlockDropsSkill extends SkillImplementation{
    public BlockDropsSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onBreak(HeroBlockBreakEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("BLOCKDROPS"));
        World world = player.getWorld();
        Block block = e.getBlock();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        for (SkillData skillData : skillDatas) {
            BlockDropsData blockDropsData = (BlockDropsData) skillData;
            skillData.ifConditionsTrue(() -> {
                Superheroes.getFoliaHacks().runASAP(block.getLocation(), () -> {
                    if (e.isDropItems()) e.setDropItems(!blockDropsData.shouldReplaceDrops());
                    if (mainHand.hasItemMeta() && mainHand.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH) && !blockDropsData.shouldReplaceDrops()) {
                        return;
                    }
                    Collection<ItemStack> drops = blockDropsData.getDrops(block.getType());
                    for (ItemStack itemStack : drops) {
                        world.dropItemNaturally(block.getLocation(), itemStack);
                    }
                });
            }, player, block.getLocation());
        }
    }
}
