package me.xemor.superheroes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Multimap;
import me.sepdron.headcreator.HeadCreator;
import me.xemor.configurationdata.CompulsoryJsonProperty;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.SoundData;
import me.xemor.configurationdata.deserializers.ItemStackDeserializer;
import me.xemor.superheroes.skills.PlusUltraSkillsContainer;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.SkillsContainer;
import me.xemor.superheroes.skills.skilldata.SkillData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Superhero {

    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();

    @JsonProperty
    private String name;
    @JsonProperty
    private String colouredName;
    @JsonProperty
    private String description;
    @JsonPropertyWithDefault
    private SoundData heroGainedSound = null;
    @JsonPropertyWithDefault
    private Skin skin = null;
    @JsonPropertyWithDefault
    @JsonDeserialize(using = ItemStackDeserializer.class)
    private ItemStack icon = null;
    @JsonPropertyWithDefault
    private SkillsContainer skills = new SkillsContainer();
    @JsonPropertyWithDefault
    @JsonDeserialize(using = PlusUltraSkillsContainer.PlusUltraSkillsContainerDeserializer.class)
    private PlusUltraSkillsContainer plusUltraSkills = new PlusUltraSkillsContainer();

    public Superhero() {}

    public Superhero(String name, String colouredName, String description) {
        this.name = name;
        this.description = description;
        this.colouredName = colouredName;
    }

    public boolean hasSkill(String skill) {
        return getSkillsAndPlusUltraSkills().containsKey(skill);
    }

    public Collection<SkillData> getSkillData(String skill) {
        return getSkillsAndPlusUltraSkills().get(skill);
    }

    public <T extends SkillData> List<T> getSkillData(Class<T> skillClazz) {
        return getSkillsAndPlusUltraSkills().get(Skill.getName(skillClazz)).stream().map(skillClazz::cast).toList();
    }

    public Collection<String> getSkills() {
        return getSkillsAndPlusUltraSkills().keys();
    }

    private Multimap<String, SkillData> getSkillsAndPlusUltraSkills() {
        Multimap<String, SkillData> skillMap = skills.getSkills();
        if (Superheroes.getInstance().hasPlusUltra()) skillMap.putAll(plusUltraSkills.getSkillsContainer().getSkills());
        return skillMap;
    }

    public String getBase64Skin() {
        if (skin == null) return null;
        return skin.value();
    }

    public String getSignature() {
        if (skin == null) return null;
        return skin.signature();
    }

    public String getPermission() { return "superheroes.hero." + name.toLowerCase();}

    public ItemStack getIcon() {
        if (icon == null) {
            Component colouredNameComponent = MiniMessage.miniMessage().deserialize(colouredName);
            icon = createSkullIcon().orElseGet(() -> createWoolIcon(colouredNameComponent));
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(legacySerializer.serialize(colouredNameComponent));
            Component descriptionComponent = MiniMessage.miniMessage().deserialize(description);
            descriptionComponent = descriptionComponent.colorIfAbsent(TextColor.color(255, 255, 255));
            meta.setLore(List.of(legacySerializer.serialize(descriptionComponent)));
            icon.setItemMeta(meta);
        }
        return icon;
    }

    private ItemStack createWoolIcon(Component colouredName) {
        TextColor color = colouredName.color();
        if (color == null) {
            return new ItemStack(Material.BLACK_WOOL);
        }
        return new ItemStack(this.woolFromColor(color.red(), color.green(), color.blue()));
    }

    private Optional<ItemStack> createSkullIcon() {
        String base64Skin = getBase64Skin();
        if (base64Skin == null || base64Skin.isEmpty()) return Optional.empty();
        return Optional.of(HeadCreator.createFromBase64(base64Skin));
    }

    private Material woolFromColor(int red, int green, int blue) {
        int distance = Integer.MAX_VALUE;
        DyeColor closest = DyeColor.BLACK;
        for (DyeColor dye : DyeColor.values()) {
            Color color = dye.getColor();
            int dist = Math.abs(color.getRed() - red) + Math.abs(color.getGreen() - green) + Math.abs(color.getBlue() - blue);
            if (dist >= distance) continue;
            distance = dist;
            closest = dye;
        }
        return Material.getMaterial((closest.name() + "_WOOL").toUpperCase());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Superhero) {
            return name.equals(((Superhero) other).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public String getColouredName() {
        return colouredName == null ? name : colouredName;
    }

    public String getDescription() {
        return description;
    }

    public Skin getSkin() {
        return skin;
    }

    public PlusUltraSkillsContainer getPlusUltraSkills() {
        return plusUltraSkills;
    }

    public SoundData getHeroGainedSound() {
        return heroGainedSound;
    }
}
