package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.SkillCooldownHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.PotionGifterData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Collection;

public class PotionGifterSkill extends SkillImplementation {

    SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public PotionGifterSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = powersHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.POTIONGIFTER);
        for (SkillData skillData : skillDatas) {
            PotionGifterData gifterData = (PotionGifterData) skillData;
            Entity entity = e.getRightClicked();
            if (skillCooldownHandler.isCooldownOver(gifterData, player.getUniqueId())) {
                if (entity instanceof LivingEntity) {
                    LivingEntity lEntity = (LivingEntity) entity;
                    World world = lEntity.getWorld();
                    world.spawnParticle(Particle.VILLAGER_HAPPY, lEntity.getLocation().add(0, 1, 0), 1);
                    lEntity.addPotionEffect(gifterData.getPotionEffect());
                    skillCooldownHandler.startCooldown(gifterData, gifterData.getCooldown(), player.getUniqueId());
                }
            }
        }
    }
}
