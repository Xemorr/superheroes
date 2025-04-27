package me.xemor.superheroes.skills;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.xemor.superheroes.skills.skilldata.SkillData;

public class SkillsContainer {

    private final Multimap<String, SkillData> skills = HashMultimap.create();

    @JsonAnySetter
    public void addSkill(String sectionName, SkillData skill) {
        skills.put(skill.getSkill(), skill);
    }

    public Multimap<String, SkillData> getSkills() {
        return skills;
    }

}
