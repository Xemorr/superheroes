package me.xemor.superheroes.skills.skilldata;

import me.xemor.skillslibrary2.conditions.ConditionList;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

public abstract class SkillData {

    private final int skill;
    private final ConfigurationSection configurationSection;
    private ConditionList conditions;

    public SkillData(int skill, @NotNull ConfigurationSection configurationSection) {
        this.skill = skill;
        this.configurationSection = configurationSection;
        ConfigurationSection conditions = configurationSection.getConfigurationSection("conditions");
        if (Superheroes.getInstance().hasSkillsLibrary()) {
            this.conditions = conditions == null ? new ConditionList() : new ConditionList(conditions);
        }
    }

    public CompletableFuture<Boolean> areConditionsTrue(Player player, Object... objects) {
        if (conditions == null) return CompletableFuture.completedFuture(true);
        return conditions.ANDConditions(player, false, objects);
    }

    public void ifConditionsTrue(Runnable runnable, Player player, Object... objects) {
        if (conditions == null) runnable.run();
        conditions.ANDConditions(player, true, objects).thenAccept((b) -> {
            if (b) runnable.run();
        });
    }

    public ConditionList getConditions() {
        if (!Superheroes.getInstance().hasSkillsLibrary()) {
            Superheroes.getInstance().getLogger().warning("PLEASE REPORT THIS MESSAGE TO XEMOR IN THE DISCORD! getConditions() called without SkillsLibrary");
        }
        return conditions;
    }

    @Nullable
    public static SkillData create(int skill, ConfigurationSection configurationSection) {
        try {
            return Skill.getClass(skill).getConstructor(int.class, ConfigurationSection.class).newInstance(skill, configurationSection);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ConfigurationSection getData() {
        return configurationSection;
    }

    public int getSkill() {
        return skill;
    }

}
