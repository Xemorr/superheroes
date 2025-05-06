package me.xemor.superheroes.language;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LanguageYaml(@JsonProperty String config_version, @JsonProperty @JsonAlias("Chat") ChatLanguageSettings chatLanguageSettings, @JsonProperty @JsonAlias("GUI") GUILanguageSettings guiLanguageSettings) {}
