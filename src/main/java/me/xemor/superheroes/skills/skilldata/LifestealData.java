package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LifestealData extends SkillData {

    @JsonAlias({"lifestealpercentage"})
    private double lifestealPercentage = 5;

    public double getLifestealPercentage() {
        return lifestealPercentage / 100;
    }
}
