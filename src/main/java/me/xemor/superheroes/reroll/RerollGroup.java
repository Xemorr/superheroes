/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.xemor.superheroes.reroll;

import me.xemor.configurationdata.comparison.ItemComparisonData;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RerollGroup {
    private int weightSum = 0;
    private List<WeightedHero> weightedHeroes = new ArrayList<>();
    @Nullable
    private ItemComparisonData itemComparator = null;

    public RerollGroup(ConfigurationSection configuration) {
        ConfigurationSection itemSection = configuration.getConfigurationSection("item");
        if (itemSection != null) {
            this.itemComparator = new ItemComparisonData(itemSection);
        }
        List<Map<?, ?>> heroes = configuration.getMapList("heroes");
        int i = 0;
        for (Map<?, ?> weightHeroMap : heroes) {
            int weight;
            String heroName = (String) weightHeroMap.get("hero");
            Object weightUnsure = weightHeroMap.get("weight");
            ++i;
            if (!(weightUnsure instanceof Integer) || (weight = (Integer) weightUnsure) <= 0) {
                Superheroes.getInstance().getLogger().severe("There is an invalid number specified at: " + configuration.getCurrentPath() + "." + i + ".hero");
                Superheroes.getInstance().getLogger().severe("This may be because it is not a number, or because it is <= 0");
                continue;
            }
            Superhero hero = Superheroes.getInstance().getHeroHandler().getSuperhero(heroName);
            if (hero == null) {
                Superheroes.getInstance().getLogger().severe("There is an invalid hero specified at: " + configuration.getCurrentPath() + "." + i + ".hero");
                continue;
            }
            weightSum += weight;
            weightedHeroes.add(new WeightedHero(hero, weight));
        }
    }

    public RerollGroup() {
    }

    private void handleEmptyWeightedHeroes() {
        if (this.weightSum == 0) {
            this.weightedHeroes = Superheroes.getInstance().getHeroHandler().getNameToSuperhero().values().stream().map(h -> new WeightedHero((Superhero) h, 1.0)).toList();
            this.weightSum = this.weightedHeroes.size();
        }
    }

    @Nullable
    public Superhero chooseHero(@NotNull Player player) {
        if (this.weightedHeroes.isEmpty()) {
            this.handleEmptyWeightedHeroes();
        }
        boolean possible = false;
        for (WeightedHero wHero : this.weightedHeroes) {
            if (!this.canUseHero(player, wHero.hero)) continue;
            possible = true;
            break;
        }
        if (!possible) {
            return null;
        }
        Superhero chosen = null;
        block1:
        while (!this.canUseHero(player, chosen)) {
            double currentWeight = 0.0;
            double rng = ThreadLocalRandom.current().nextDouble();
            for (WeightedHero wHero : this.weightedHeroes) {
                if (!(rng <= (currentWeight += wHero.weight / this.weightSum))) continue;
                chosen = wHero.hero;
                continue block1;
            }
        }
        return chosen;
    }

    private boolean canUseHero(@NotNull Player player, @Nullable Superhero hero) {
        if (hero == null) {
            return false;
        }
        return !Superheroes.getInstance().getRerollHandler().doesHeroRequirePermissions() || player.hasPermission(hero.getPermission());
    }

    public boolean matchesItem(ItemStack item) {
        if (this.itemComparator == null) {
            return false;
        }
        return this.itemComparator.matches(item);
    }

    private record WeightedHero(Superhero hero, double weight) {
    }
}

