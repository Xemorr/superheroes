package me.xemor.superheroes;

import me.xemor.superheroes.skills.SkillsContainer;
import me.xemor.superheroes.skills.skilldata.SkillData;

import java.util.Collection;

public record Superhero(String name, String colouredName, String description, Skin skin, SkillsContainer skills) {

    public boolean hasSkill(String skill) {
        return skills.getSkills().containsKey(skill);
    }

    public Collection<SkillData> getSkillData(String skill) {
        return skills.getSkills().get(skill);
    }

    public Collection<String> getSkills() {
        return skills.getSkills().keys();
    }

    public String getBase64Skin() {
        return skin.value();
    }

    public String getSignature() {
        return skin.signature();
    }

    public String getPermission() { return "superheroes.hero." + name.toLowerCase();}

    @Override
    public boolean equals(Object other) {
        if (other instanceof Superhero) {
            return name.equals(((Superhero) other).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
