package me.xemor.superheroes2.skills.skilldata;

import me.xemor.skillslibrary.conditions.*;
import me.xemor.superheroes2.Superheroes2;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.exceptions.InvalidConfig;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SkillData {

    private final int skill;
    private final ConfigurationSection configurationSection;
    private final List<Condition> conditions = new ArrayList<>();

    public SkillData(int skill, ConfigurationSection configurationSection) {
        this.skill = skill;
        this.configurationSection = configurationSection;
        ConfigurationSection conditions = configurationSection.getConfigurationSection("conditions");
        if (conditions != null && Superheroes2.getInstance().hasSkillsLibrary()) {
            loadConditions(conditions);
        }
    }


    private void loadConditions(ConfigurationSection conditionsSection) {
        if (conditionsSection == null) return;
        Map<String, Object> values = conditionsSection.getValues(false);
        for (Object item : values.values()) {
            if (item instanceof ConfigurationSection) {
                ConfigurationSection conditionSection = (ConfigurationSection) item;
                String type = conditionSection.getString("type", "");
                int condition = Conditions.getCondition(type);
                if (condition == -1) {
                    try {
                        throw new InvalidConfig("Invalid Condition Type " + conditionSection.getCurrentPath() + ".type is " + type);
                    } catch(InvalidConfig e) {
                        e.printStackTrace();
                    }
                }
                Condition conditionData = Condition.create(condition, conditionSection);
                if (conditionData != null) {
                    conditions.add(conditionData);
                }
            }
        }
    }

    public boolean areConditionsTrue(Player player) {
        for (Condition condition : conditions) {
            if (condition instanceof EntityCondition) {
                EntityCondition entityCondition = (EntityCondition) condition;
                if (!entityCondition.isTrue(player)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean areConditionsTrue(Player player, Block block) {
        for (Condition condition : conditions) {
            if (condition instanceof EntityCondition) {
                EntityCondition entityCondition = (EntityCondition) condition;
                if (!entityCondition.isTrue(player)) {
                    return false;
                }
            }
            else if (condition instanceof BlockCondition) {
                BlockCondition blockCondition = (BlockCondition) condition;
                if (!blockCondition.isTrue(player, block)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean areConditionsTrue(Player player, Entity entity) {
        for (Condition condition : conditions) {
            if (condition instanceof EntityCondition) {
                EntityCondition entityCondition = (EntityCondition) condition;
                if (!entityCondition.isTrue(player)) {
                    return false;
                }
            }
            else if (condition instanceof TargetCondition) {
                TargetCondition targetCondition = (TargetCondition) condition;
                if (!targetCondition.isTrue(player, entity)) {
                    return false;
                }
            }
        }
        return true;
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
