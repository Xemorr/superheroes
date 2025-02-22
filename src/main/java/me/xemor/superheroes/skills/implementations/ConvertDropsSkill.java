package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.ConvertDropsData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ConvertDropsSkill extends SkillImplementation{
    public ConvertDropsSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onBreak(BlockDropItemEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("CONVERTDROPS"));
        BlockState block = e.getBlockState();
        if (block instanceof Container) {
            return;
        }
        for (SkillData skillData : skillDatas) {
            ConvertDropsData convertDropsData = (ConvertDropsData) skillData;
            if (convertDropsData.getIgnoredBlocks().contains(block.getType())) {
                continue;
            }
            if (!convertDropsData.shouldIgnoreSilkTouch()) {
                ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                if (item.containsEnchantment(Enchantment.SILK_TOUCH)) continue;
            }
            skillData.ifConditionsTrue(() -> {
                List<Item> drops = e.getItems();
                Map<Material, ItemStack> dropToNewDrop = convertDropsData.getDropToNewDrop();
                for (Item item : drops) {
                    ItemStack itemStack = item.getItemStack();
                    int amount = itemStack.getAmount();
                    ItemStack resultingItem = dropToNewDrop.get(itemStack.getType());
                    ItemStack toDrop;
                    if (resultingItem != null) {
                        toDrop = resultingItem.clone();
                        toDrop.setAmount(amount * toDrop.getAmount());
                        item.setItemStack(toDrop);
                    }
                }
            }, player, block.getLocation());

        }
    }
}