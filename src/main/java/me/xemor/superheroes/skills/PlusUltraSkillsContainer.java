package me.xemor.superheroes.skills;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.xemor.superheroes.Superheroes;

import java.io.IOException;

@JsonDeserialize(using = PlusUltraSkillsContainer.PlusUltraSkillsContainerDeserializer.class)
public class PlusUltraSkillsContainer {

    private SkillsContainer skillsContainer = new SkillsContainer();

    public PlusUltraSkillsContainer(SkillsContainer skillsContainer) {
        this.skillsContainer = skillsContainer;
    }

    public PlusUltraSkillsContainer() {}

    public SkillsContainer getSkillsContainer() {
        return skillsContainer;
    }

    public static class PlusUltraSkillsContainerDeserializer extends JsonDeserializer<PlusUltraSkillsContainer> {
        @Override
        public PlusUltraSkillsContainer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            if (Superheroes.getInstance().getServer().getPluginManager().getPlugin("SuperheroesPlusUltra") != null) {
                return new PlusUltraSkillsContainer(ctxt.readValue(p, SkillsContainer.class));
            }
            return new PlusUltraSkillsContainer();
        }
    }

}
