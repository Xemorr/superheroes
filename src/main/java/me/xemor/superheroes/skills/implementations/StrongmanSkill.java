package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.StrongmanData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class StrongmanSkill extends SkillImplementation {

    public StrongmanSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("STRONGMAN"));
        for (SkillData skillData : skillDatas) {
            StrongmanData strongmanData = (StrongmanData) skillData;
            Entity topEntity = getTopEntity(e.getPlayer());
            if (skillData.areConditionsTrue(player, e.getRightClicked())) {
                if (!e.getRightClicked().equals(topEntity) && countPassengers(topEntity) <= strongmanData.getMaxPassengers()) {
                    topEntity.addPassenger(e.getRightClicked());
                }
                if (e.getRightClicked() instanceof Vehicle) {
                    Component tooMuscular = MiniMessage.miniMessage().deserialize(strongmanData.getTooMuscularMessage(), Placeholder.unparsed("player", player.getName()));
                    Audience playerAudience = Superheroes.getBukkitAudiences().player(player);
                    playerAudience.sendActionBar(tooMuscular);
                }
            }
        }
    }

    public Entity getTopEntity(Entity entity) {
        if (!entity.getPassengers().isEmpty()) {
            return getTopEntity(entity.getPassengers().get(0));
        }
        else {
            return entity;
        }
    }

    public int countPassengers(Entity entity) {
        int x = 0;
        if (!entity.getPassengers().isEmpty()) {
            return countPassengers(entity.getPassengers().get(0), ++x);
        }
        else {
            return x;
        }
    }

    public int countPassengers(Entity entity, int x) {
        if (!entity.getPassengers().isEmpty()) {
            return countPassengers(entity.getPassengers().get(0), ++x);
        }
        else {
            return x;
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("STRONGMAN"));
        for (SkillData skillData : skillDatas) {
            StrongmanData strongmanData = (StrongmanData) skillData;
            if (e.isSneaking()) {
                while (!player.getPassengers().isEmpty()) {
                    Entity topEntity = getTopEntity(player);
                    Vector velocity = player.getEyeLocation().getDirection().normalize();
                    velocity.setX(velocity.getX() * strongmanData.getVelocity());
                    velocity.setZ(velocity.getZ() * strongmanData.getVelocity());
                    velocity.setY(velocity.getY() * strongmanData.getUpwardsVelocity());
                    topEntity.getVehicle().removePassenger(topEntity);
                    Superheroes.getScheduling().entitySpecificScheduler(topEntity).runDelayed(
                            () -> topEntity.setVelocity(velocity), () -> {}, 1L
                    );
                }
            }
        }
    }

    @EventHandler
    public void onLostPower(PlayerChangedSuperheroEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = e.getOldHero();
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("STRONGMAN"));
        for (SkillData skillData : skillDatas) {
            StrongmanData strongmanData = (StrongmanData) skillData;
            while (player.getPassengers().size() > 0) {
                Entity topEntity = getTopEntity(player);
                Vector velocity = player.getEyeLocation().getDirection().normalize();
                velocity.setX(velocity.getX() * strongmanData.getVelocity());
                velocity.setZ(velocity.getZ() * strongmanData.getVelocity());
                velocity.setY(velocity.getY() * strongmanData.getUpwardsVelocity());
                topEntity.getVehicle().removePassenger(topEntity);
                Superheroes.getScheduling().entitySpecificScheduler(topEntity).runDelayed(
                        () -> topEntity.setVelocity(velocity), () -> {}, 1L
                );
            }
        }
    }
}
