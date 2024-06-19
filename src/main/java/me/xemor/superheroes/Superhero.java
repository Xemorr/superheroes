package me.xemor.superheroes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class Superhero {

    protected final String name;
    protected final String colouredName;
    protected final String description;
    private String base64Skin;
    private String signature;
    private boolean isLocked; // Indicate whether hero has entered read-only mode - don't add skills once created

    private ItemStack icon;
    protected final Multimap<Integer, SkillData> skillToData = HashMultimap.create();

    public Superhero(String name, String colouredName, String description) {
        this.name = name;
        this.colouredName = colouredName;
        this.description = description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void addSkill(SkillData skill) {
        if (!isLocked) {
            skillToData.put(skill.getSkill(), skill);
        }
        else {
            throw new IllegalStateException("This Superhero has entered read-only mode");
        }
    }

    public void addSkills(SkillData... skills) {
        for (SkillData skill : skills) {
            addSkill(skill);
        }
    }

    public boolean hasSkill(int skill) {
        isLocked = true;
        return skillToData.containsKey(skill);
    }

    public Collection<SkillData> getSkillData(int skill) {
        isLocked = true;
        return skillToData.get(skill);
    }

    public Collection<Integer> getSkills() {
        isLocked = true;
        return skillToData.keys();
    }

    public String getColouredName() {
        return colouredName;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getBase64Skin() {
        return base64Skin == null ? "" : base64Skin;
    }

    public void setBase64Skin(String base64Skin) {
        this.base64Skin = base64Skin;
    }

    public String getSignature() {
        return signature == null ? "" : signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPermission() { return "superheroes.hero." + getName().toLowerCase();}

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

}
