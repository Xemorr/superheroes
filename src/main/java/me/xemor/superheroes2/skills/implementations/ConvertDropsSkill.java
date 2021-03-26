package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.ConvertDropsData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class ConvertDropsSkill extends SkillImplementation{
    public ConvertDropsSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.CONVERTDROPS);
        World world = player.getWorld();
        Block block = e.getBlock();
        if (block.getState() instanceof Container) {
            return;
        }
        for (SkillData skillData : skillDatas) {
            ConvertDropsData convertDropsData = (ConvertDropsData) skillData;
            if (convertDropsData.getIgnoredBlocks().contains(block.getType())) {
                continue;
            }
            e.setDropItems(false);
            Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand(), player);
            Map<Material, ItemStack> dropToNewDrop = convertDropsData.getDropToNewDrop();
            for (ItemStack itemStack : drops) {
                int amount = itemStack.getAmount();
                ItemStack resultingItem = dropToNewDrop.get(itemStack.getType());
                ItemStack toDrop = itemStack;
                if (resultingItem != null) {
                    toDrop = resultingItem.clone();
                    toDrop.setAmount(amount * toDrop.getAmount());
                }
                world.dropItemNaturally(block.getLocation(), toDrop);
            }
        }
    }
}
