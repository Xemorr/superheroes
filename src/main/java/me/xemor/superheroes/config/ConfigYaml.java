package me.xemor.superheroes.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.xemor.superheroes.Superhero;

import java.util.List;

public record ConfigYaml(@JsonProperty String config_version,
                         @JsonProperty PowerOnStartSettings powerOnStart,
                         @JsonProperty GUISettings gui,
                         @JsonProperty HeroCommandSettings heroCommand,
                         @JsonProperty Superhero defaultHero,
                         @JsonProperty List<String> disabledWorlds) {
}
