package me.xemor.superheroes2.skills.implementations;

import de.themoep.minedown.MineDown;
import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.SkillCooldownHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.PotionGifterSkillData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import net.md_5.bungee.api.chat.BaseComponent;
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

    public PotionGifterSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.POTIONGIFTER);
        for (SkillData skillData : skillDatas) {
            PotionGifterSkillData gifterData = (PotionGifterSkillData) skillData;
            Entity entity = e.getRightClicked();
            if (skillCooldownHandler.isCooldownOver(gifterData, player.getUniqueId())) {
                if (entity instanceof LivingEntity) {
                    LivingEntity lEntity = (LivingEntity) entity;
                    World world = lEntity.getWorld();
                    world.spawnParticle(Particle.VILLAGER_HAPPY, lEntity.getLocation().add(0, 1, 0), 1);
                    lEntity.addPotionEffect(gifterData.getPotionEffect());
                    skillCooldownHandler.startCooldown(gifterData, gifterData.getCooldown(), player.getUniqueId());
                    BaseComponent[] giverMessage = new MineDown(gifterData.getGiverMessage()).replace("player", player.getDisplayName()).toComponent();
                    player.spigot().sendMessage(giverMessage);
                    if (lEntity instanceof Player) {
                        Player receiver = (Player) lEntity;
                        MineDown mineDown = new MineDown(gifterData.getReceiverMessage());
                        mineDown = mineDown.replaceFirst(true).replace("gifter", player.getDisplayName());
                        receiver.spigot().sendMessage(mineDown.toComponent());
                    }
                }
            }
        }
    }
}
