package me.xemor.superheroes2;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;

import java.util.Collection;
import java.util.Collections;

public class Superhero {

    protected String name;
    protected String colouredName;
    protected String description;
    protected Multimap<Skill, SkillData> skillToData = HashMultimap.create();

    public Superhero(String name, String colouredName, String description) {
        this.name = name;
        this.colouredName = colouredName;
        this.description = description;
    }

    public void addSkill(SkillData skill) {
        skillToData.put(skill.getSkill(), skill);
    }

    public void addSkills(SkillData... skills) {
        for (SkillData skill : skills) {
            addSkill(skill);
        }
    }

    public boolean hasSkill(Skill skill) {
        return skillToData.containsKey(skill);
    }

    public Collection<SkillData> getSkillData(Skill skill) {
        Collection<SkillData> skillData = skillToData.get(skill);
        return skillData == null ? Collections.emptyList() : skillData;
    }

    public Collection<Skill> getSkills() {
        return skillToData.keys();
    }

    public String getColouredName() {
        return colouredName;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPermission() { return "superheroes.hero." + getName().toLowerCase();}

}
