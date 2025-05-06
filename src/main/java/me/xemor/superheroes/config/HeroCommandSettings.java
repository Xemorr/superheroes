package me.xemor.superheroes.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record HeroCommandSettings(@JsonProperty double cooldown, @JsonProperty List<String> aliases) {}
