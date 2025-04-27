package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class WeatherDamageData extends SkillData {

    @JsonPropertyWithDefault
    private double damage = 1.0;
    @JsonPropertyWithDefault
    private boolean checkShelter = true;

    public double getDamage() {
        return damage;
    }

    public boolean checkShelter() {
        return checkShelter;
    }
}
