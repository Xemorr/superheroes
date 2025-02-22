package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.WeatherDamageData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class WeatherDamageSkill extends SkillImplementation {

    public WeatherDamageSkill(HeroHandler heroHandler) {
        super(heroHandler);
        Superheroes.getScheduling().globalRegionalScheduler().runAtFixedRate(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Superheroes.getScheduling().entitySpecificScheduler(player).run(() -> applyDamage(player), () -> {});
            }
        }, 1L, 20L);
    }

    public void applyDamage(Player player) {
        Superhero superhero = heroHandler.getSuperhero(player);
        List<WeatherDamageData> weatherDamageDatas = superhero.getSkillData(WeatherDamageData.class);
        for (WeatherDamageData weatherDamageData : weatherDamageDatas) {
            weatherDamageData.ifConditionsTrue(() -> {
                if (!player.getWorld().isClearWeather()) {
                    if (weatherDamageData.checkShelter()) {
                        if (player.getWorld().getBlockAt(player.getLocation()).getLightFromSky() != 15) {
                            return;
                        }
                    }
                    player.damage(weatherDamageData.getDamage());
                }
            }, player);
        }
    }
}
