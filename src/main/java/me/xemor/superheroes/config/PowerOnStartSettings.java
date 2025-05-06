package me.xemor.superheroes.config;

import com.fasterxml.jackson.annotation.JsonAlias;

public record PowerOnStartSettings(@JsonAlias("isEnabled") boolean enabled, @JsonAlias("showHero") boolean firstJoinTitle) {}
