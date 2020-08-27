package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.StrongmanData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.Collection;

public class StrongmanSkill extends SkillImplementation {

    public StrongmanSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = powersHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.STRONGMAN);
        for (SkillData skillData : skillDatas) {
            StrongmanData strongmanData = (StrongmanData) skillData;
            Entity topEntity = getTopEntity(e.getPlayer());
            if (!e.getRightClicked().equals(topEntity) && countPassengers(topEntity) <= strongmanData.getMaxPassengers()) {
                topEntity.addPassenger(e.getRightClicked());
            }
            if (e.getRightClicked() instanceof Vehicle) {
                e.getPlayer().sendMessage(superhero.getColouredName() + strongmanData.getTooMuscularMessage());
            }
        }
    }

    public Entity getTopEntity(Entity entity) {
        if (entity.getPassengers().size() > 0) {
            return getTopEntity(entity.getPassengers().get(0));
        }
        else {
            return entity;
        }
    }

    public int countPassengers(Entity entity) {
        int x = 0;
        if (entity.getPassengers().size() > 0) {
            return countPassengers(entity.getPassengers().get(0), ++x);
        }
        else {
            return x;
        }
    }

    public int countPassengers(Entity entity, int x) {
        if (entity.getPassengers().size() > 0) {
            return countPassengers(entity.getPassengers().get(0), ++x);
        }
        else {
            return x;
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = powersHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.STRONGMAN);
        for (SkillData skillData : skillDatas) {
            StrongmanData strongmanData = (StrongmanData) skillData;
            if (e.isSneaking()) {
                while (player.getPassengers().size() > 0) {
                    Entity topEntity = getTopEntity(player);
                    Vector velocity = player.getEyeLocation().getDirection().normalize();
                    velocity.setX(velocity.getX() * strongmanData.getVelocity());
                    velocity.setZ(velocity.getZ() * strongmanData.getVelocity());
                    velocity.setY(velocity.getY() * strongmanData.getUpwardsVelocity());
                    topEntity.setVelocity(velocity);
                    topEntity.getVehicle().removePassenger(topEntity);
                }
            }
        }
    }

    @EventHandler
    public void onLostPower(PlayerLostSuperheroEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = powersHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.STRONGMAN);
        for (SkillData skillData : skillDatas) {
            StrongmanData strongmanData = (StrongmanData) skillData;
            while (player.getPassengers().size() > 0) {
                Entity topEntity = getTopEntity(player);
                topEntity.getVehicle().removePassenger(topEntity);
            }
        }
    }
}
