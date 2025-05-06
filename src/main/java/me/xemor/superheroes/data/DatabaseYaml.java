package me.xemor.superheroes.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DatabaseYaml(@JsonProperty DatabaseSettings database, @JsonProperty String config_version) {}
