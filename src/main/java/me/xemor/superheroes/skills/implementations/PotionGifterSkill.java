package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.PotionGifterSkillData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Collection;

public class PotionGifterSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public PotionGifterSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("POTIONGIFTER"));
        for (SkillData skillData : skillDatas) {
            PotionGifterSkillData gifterData = (PotionGifterSkillData) skillData;
            Entity entity = e.getRightClicked();
            if (skillCooldownHandler.isCooldownOver(gifterData, player.getUniqueId())) {
                if (entity instanceof LivingEntity lEntity) {
                    if (skillData.areConditionsTrue(player, lEntity)) {
                        World world = lEntity.getWorld();
                        world.spawnParticle(Particle.HAPPY_VILLAGER, lEntity.getLocation().add(0, 1, 0), 1);
                        gifterData.getPotionEffect().ifPresent(lEntity::addPotionEffect);
                        skillCooldownHandler.startCooldown(gifterData, gifterData.getCooldown(), player.getUniqueId());
                        Component giverMessage = MiniMessage.miniMessage().deserialize(gifterData.getGiverMessage(), Placeholder.unparsed("player",player.getName()));
                        Superheroes.getBukkitAudiences().player(player).sendMessage(giverMessage);
                        if (lEntity instanceof Player receiver) {
                            Component receiverMessage = MiniMessage.miniMessage().deserialize(gifterData.getReceiverMessage(),
                                    Placeholder.unparsed("gifter", player.getName()),
                                    Placeholder.unparsed("player", receiver.getName()));
                            Superheroes.getBukkitAudiences().player(receiver).sendMessage(receiverMessage);
                        }
                    }
                }
            }
        }
    }
}
