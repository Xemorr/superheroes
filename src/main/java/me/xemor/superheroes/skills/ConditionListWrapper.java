package me.xemor.superheroes.skills;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.xemor.skillslibrary2.conditions.ConditionList;
import me.xemor.superheroes.Superheroes;

import java.io.IOException;
import java.util.Optional;

@JsonDeserialize(using = ConditionListWrapper.ConditionListWrapperDeserializer.class)
public class ConditionListWrapper {

    @JsonIgnore
    private ConditionList conditionList;

    public ConditionListWrapper(ConditionList conditionList) {this.conditionList = conditionList;}
    public ConditionListWrapper() {conditionList = null;}

    public Optional<ConditionList> getConditionListIfSkillsLibraryPresent() {
        return Optional.ofNullable(conditionList);
    }

    public static class ConditionListWrapperDeserializer extends JsonDeserializer<ConditionListWrapper> {

        public ConditionListWrapperDeserializer() {}

        @Override
        public ConditionListWrapper deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (Superheroes.getInstance().hasSkillsLibrary()) {
                return new ConditionListWrapper(ctxt.readValue(p, ConditionList.class));
            }
            return null;
        }
    }
}
