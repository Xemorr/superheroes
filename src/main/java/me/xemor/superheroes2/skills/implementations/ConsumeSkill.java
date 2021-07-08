package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.ConsumeSkillData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ConsumeSkill extends SkillImplementation {

    public ConsumeSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("CONSUME"));
        for (SkillData skillData : skillDatas) {
            ConsumeSkillData consumeData = (ConsumeSkillData) skillData;
            if (consumeData.areConditionsTrue(player)) {
                ItemStack item = e.getItem();
                if (item == null) return;
                if (item.getType() == consumeData.getMaterial()) {
                    if (consumeData.getPotionEffect() != null) {
                        player.addPotionEffect(consumeData.getPotionEffect());
                    }
                    player.setFoodLevel(player.getFoodLevel() + consumeData.getHunger());
                    if (item.getAmount() == 1) {
                        player.getInventory().setItemInMainHand(null);
                    }
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
    }


}
