package me.xemor.superheroes.config;

import com.fasterxml.jackson.annotation.JsonAlias;

public record GUISettings(@JsonAlias("onStart") boolean startsOpen, @JsonAlias("canClose") boolean closeable) {
}
