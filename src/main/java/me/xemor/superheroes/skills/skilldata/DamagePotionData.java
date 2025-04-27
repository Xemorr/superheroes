package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.PotionEffectData;
import me.xemor.configurationdata.comparison.SetData;
import me.xemor.superheroes.skills.skilldata.exceptions.InvalidConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DamagePotionData extends SkillData {

    @JsonUnwrapped
    private PotionEffectData potionEffect;
    @JsonPropertyWithDefault
    @JsonAlias("damageCause")
    private SetData<EntityDamageEvent.DamageCause> damageCauses = new SetData<>();

    public Optional<PotionEffect> getPotionEffect() {
        if (potionEffect == null) return Optional.empty();
        return potionEffect.createPotion();
    }

    public boolean isValidDamageCause(EntityDamageEvent.DamageCause damageCause) {
        return damageCauses.inSet(damageCause);
    }
}