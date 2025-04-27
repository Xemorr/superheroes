package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.*;
import me.xemor.configurationdata.AttributesData;
import me.xemor.configurationdata.ConfigurationData;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Map;

public class AttributeSkillData extends SkillData {

    @JsonIgnore
    private AttributesData attributes;

    @JsonCreator
    public AttributeSkillData(
            @JsonProperty("attributes") Map<Attribute, Double> attributes,
            @JsonProperty("equipmentSlot") @JsonAlias("equipment_slot") EquipmentSlotGroup equipmentSlot,
            @JsonProperty("operation") AttributesData.Operation operation,
            @JsonProperty("uniqueKey") @JsonAlias("unique_key") String uniqueKey
    ) {
        if (attributes.remove(null) != null) {
            ConfigurationData.getLogger()
                    .severe("AttributeSkillData has a null key! Please see valid attributes here: "
                            + Registry.ATTRIBUTE.stream().map((attr) -> attr.getKey().getKey()).reduce((s1, s2) -> s1 + ", " + s2).orElse("")
                    );
        }
        this.attributes = new AttributesData(attributes, equipmentSlot, operation, uniqueKey);
    }

    public AttributesData getAttributes() {
        return attributes;
    }
}
