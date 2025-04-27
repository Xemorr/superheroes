package me.xemor.superheroes.reroll;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.xemor.configurationdata.ConfigurationData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
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

    @JsonIgnore
    private int weightSum = -1;
    @JsonPropertyWithDefault
    @JsonAlias("heroes")
    private List<WeightedHero> weightedHeroes = new ArrayList<>();
    @JsonPropertyWithDefault
    @JsonAlias("item")
    private ItemComparisonData itemComparator = null;

    public RerollGroup() {}

    private void handleEmptyWeightedHeroes() {
        if (this.weightSum == 0) {
            this.weightedHeroes = Superheroes.getInstance().getHeroHandler().getNameToSuperhero().values().stream().map(h -> new WeightedHero(h, 1)).toList();
            this.weightSum = this.weightedHeroes.size();
        }
    }

    @Nullable
    public Superhero chooseHero(@NotNull Player player) {
        if (weightSum == -1) {
            weightSum = weightedHeroes.stream().map((it) -> it.weight).reduce(Integer::sum).orElse(0);
        }
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
                if (!(rng <= (currentWeight += (double) wHero.weight / this.weightSum))) continue;
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
        return !Superheroes.getInstance().getRerollHandler().getRerollConfig().getGlobalRerollSettings().doesEachHeroRequirePermissions() || player.hasPermission(hero.getPermission());
    }

    public boolean matchesItem(ItemStack item) {
        if (this.itemComparator == null) {
            return false;
        }
        return this.itemComparator.matches(item);
    }

    private record WeightedHero(Superhero hero, int weight) {

        @JsonCreator
        public WeightedHero(@JsonProperty("hero") String hero, @JsonProperty("weight") int weight) {
            this(Superheroes.getInstance().getHeroHandler().getSuperhero(hero), weight);
        }

    }
}

