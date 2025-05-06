package me.xemor.superheroes.language;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class GUILanguageSettings {

    @JsonPropertyWithDefault
    private String name = "Pick your hero!";

    public String getName() {
        return name;
    }
}
