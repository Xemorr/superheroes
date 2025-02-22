package me.xemor.superheroes.conditions;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.conditions.Condition;
import me.xemor.skillslibrary2.conditions.EntityCondition;
import me.xemor.skillslibrary2.conditions.TargetCondition;
import me.xemor.skillslibrary2.execution.Execution;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SuperheroCondition extends Condition implements EntityCondition, TargetCondition {

    final Set<String> heroes;

    public SuperheroCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        heroes = configurationSection.getStringList("heroes").stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    public SuperheroCondition(int condition, String heroName) {
        super(condition, Mode.SELF);
        heroes = new HashSet<>();
        heroes.add(heroName.toLowerCase());
    }

    @Override
    public boolean isTrue(Execution execution, Entity entity) {
        return checkHero(entity);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Execution execution, Entity entity, Entity target) {
        return CompletableFuture.completedFuture(checkHero(target));
    }

    public boolean checkHero(Entity entity) {
        if (entity instanceof Player player) {
            Superhero superhero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
            return heroes.contains(superhero.getName().toLowerCase());
        }
        return false;
    }

}

