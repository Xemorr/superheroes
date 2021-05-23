package me.xemor.superheroes2.skills.skilldata.configdata;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class AttributeData {

    EnumMap<Attribute, AttributeModifier> attributeToValue = new EnumMap<>(Attribute.class);

    public AttributeData(ConfigurationSection configurationSection) {
        Attribute[] attributes = Attribute.values();
        for (Attribute attribute : attributes) {
            String attributeName = attribute.toString();
            if (attributeName.startsWith("GENERIC_")) {
                attributeName = attributeName.substring(8);
            }
            ConfigurationSection attributeSection = configurationSection.getConfigurationSection(attributeName);
            if (attributeSection != null) {
                double value = attributeSection.getDouble("value", 0);
                EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(attributeSection.getString("equipmentslot", "HAND"));
                AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), "Superheroes2 Attribute", value, AttributeModifier.Operation.ADD_NUMBER, equipmentSlot);
                attributeToValue.put(attribute, attributeModifier);
            }
        }
    }

    public AttributeData() {
    }

    public void applyAttributes(LivingEntity livingEntity) {
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance instance = livingEntity.getAttribute(attribute);
            if (instance != null) {
                instance.setBaseValue(getValue(livingEntity, attribute));
            }
        }
    }

    public void applyAttributes(ItemMeta meta) {
        for (Map.Entry<Attribute, AttributeModifier> item : attributeToValue.entrySet()) {
            meta.addAttributeModifier(item.getKey(), item.getValue());
        }
    }

    public double getValue(LivingEntity livingEntity, Attribute attribute) {
        AttributeModifier attributeModifier = attributeToValue.get(attribute);
        if (attributeModifier == null) {
            return -1;
        }
        return attributeModifier.getAmount();
    }

}
