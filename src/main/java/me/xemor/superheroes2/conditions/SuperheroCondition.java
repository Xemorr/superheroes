package me.xemor.superheroes2.conditions;

import me.xemor.skillslibrary.conditions.Condition;
import me.xemor.skillslibrary.conditions.EntityCondition;
import me.xemor.skillslibrary.conditions.TargetCondition;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.Superheroes2;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class SuperheroCondition extends Condition implements EntityCondition, TargetCondition {

    Set<String> heroes;

    public SuperheroCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        heroes = configurationSection.getStringList("heroes").stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    @Override
    public boolean isTrue(LivingEntity livingEntity) {
        return checkHero(livingEntity);
    }

    @Override
    public boolean isTrue(LivingEntity livingEntity, Entity entity) {
        return checkHero(entity);
    }

    public boolean checkHero(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Superhero superhero = Superheroes2.getInstance().getHeroHandler().getSuperhero(player);
            return heroes.contains(superhero.getName().toLowerCase());
        }
        return false;
    }

}

