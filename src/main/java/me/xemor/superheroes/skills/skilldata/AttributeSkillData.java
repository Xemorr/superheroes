package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.AttributeData;
import me.xemor.superheroes.Superheroes;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class AttributeSkillData extends SkillData {

    private final AttributeData attributeData;

    public AttributeSkillData(int skill, @NotNull ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        ConfigurationSection attributesSection = configurationSection.getConfigurationSection("attributes");
        if (attributesSection == null) {
            Superheroes.getInstance().getLogger().severe("You have not specified an attributes section! " + configurationSection.getCurrentPath() + ".attributes");
        }
        attributeData = new AttributeData(attributesSection, Superheroes.getInstance().getName());
        if (Superheroes.getInstance().hasSkillsLibrary() && this.getConditions().iterator().hasNext()) {
            Superheroes.getInstance().getLogger().warning("Attribute skill does not support conditions! Your conditions will not work. " + configurationSection.getCurrentPath() + ".conditions");
        }
    }

    public AttributeData getAttributeData() {
        return attributeData;
    }
}
