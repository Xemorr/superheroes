package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.ConsumeSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ConsumeSkill extends SkillImplementation {

    public ConsumeSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent e) {
        if (e.useItemInHand() == Event.Result.DENY) return;
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData("CONSUME");
        for (SkillData skillData : skillDatas) {
            ConsumeSkillData consumeData = (ConsumeSkillData) skillData;
            consumeData.ifConditionsTrue(() -> {
                ItemStack item = e.getItem();
                if (item == null) return;
                if (item.getType() == consumeData.getMaterial()) {
                    consumeData.getPotionEffect().ifPresent(player::addPotionEffect);
                    player.setFoodLevel(Math.min(20, player.getFoodLevel() + consumeData.getHunger()));
                    item.setAmount(item.getAmount() - 1);
                }
            }, player);
        }
    }


}
