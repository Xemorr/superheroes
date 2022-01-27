package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.WeatherDamageData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class WeatherDamageSkill extends SkillImplementation {

    public WeatherDamageSkill(HeroHandler heroHandler) {
        super(heroHandler);
        Bukkit.getScheduler().runTaskTimer(heroHandler.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyDamage(player);
            }
        }, 0, 20);
    }

    public void applyDamage(Player player) {
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("WEATHERDAMAGE"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                if (skillData.areConditionsTrue(player)) {
                    if (!player.getWorld().isClearWeather()) {
                        WeatherDamageData weatherDamageData = (WeatherDamageData) skillData;
                        if (weatherDamageData.checkShelter()) {
                            if (player.getWorld().getBlockAt(player.getLocation()).getLightFromSky() != 15) {
                                return;
                            }
                        }
                        player.damage(weatherDamageData.getDamage());
                    }
                }
            }
        }
    }
}
