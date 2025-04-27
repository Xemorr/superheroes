package me.xemor.superheroes.skills;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.xemor.superheroes.skills.skilldata.*;
import me.xemor.superheroes.skills.skilldata.spell.SpellData;

import java.util.Collection;
import java.util.Map;

public class Skill {

    private static int counter = 0;
    private static final BiMap<String, Class<? extends SkillData>> skillToData = HashBiMap.create();

    static {
        registerSkill("DAMAGEMODIFIER", DamageModifierData.class);
        registerSkill("ATTRIBUTE", AttributeSkillData.class);
        registerSkill("FLIGHT", BlankData.class);
        registerSkill("POTIONEFFECT", PotionEffectSkillData.class);
        registerSkill("OHKO", OHKOData.class);
        registerSkill("REPULSION", RepulsionData.class);
        registerSkill("CONVERTITEM", ConvertItemData.class);
        registerSkill("CONVERTBLOCK", ConvertBlockData.class);
        registerSkill("REMOTEDETONATION", RemoteDetonationData.class);
        registerSkill("BLOCKDROPS", BlockDropsData.class);
        registerSkill("CREEPER", CreeperData.class);
        registerSkill("GIVEITEM", GiveItemData.class);
        registerSkill("GUN", GunData.class);
        registerSkill("SNEAK", SneakData.class);
        registerSkill("SHIELD", ShieldData.class);
        registerSkill("SPELL", SpellData.class);
        registerSkill("THROWER", ThrowerData.class);
        registerSkill("BLOCKRAY", BlockRayData.class);
        registerSkill("POTIONGIFTER", PotionGifterSkillData.class);
        registerSkill("CONSUME", ConsumeSkillData.class);
        registerSkill("SUMMON", SummonSkillData.class);
        registerSkill("DECOY", DecoyData.class);
        registerSkill("PHASE", PhaseData.class);
        registerSkill("STRONGMAN", StrongmanData.class);
        registerSkill("CRAFTING", CraftingData.class);
        registerSkill("SLAM", SlamData.class);
        registerSkill("ERASER", EraserData.class);
        registerSkill("TELEPORT", TeleportData.class);
        registerSkill("INSTANTBREAK", InstantBreakData.class);
        registerSkill("LIGHT", LightSkillData.class);
        registerSkill("NOHUNGER", NoHungerData.class);
        registerSkill("DAMAGERESISTANCE", DamageResistanceData.class);
        registerSkill("SLIME", SlimeData.class);
        registerSkill("SNEAKINGPOTION", SneakingPotionData.class);
        registerSkill("AURA", AuraData.class);
        registerSkill("EGGLAYER", EggLayerData.class);
        registerSkill("WALKER", WalkerData.class);
        registerSkill("PICKPOCKET", PickpocketData.class);
        registerSkill("CONVERTDROPS", ConvertDropsData.class);
        registerSkill("LIFESTEAL", LifestealData.class);
        registerSkill("DAMAGEPOTION", DamagePotionData.class);
        registerSkill("WEATHERDAMAGE", WeatherDamageData.class);
        registerSkill("HEARTSTEAL", HeartStealData.class);
        registerSkill("KILLPOTION", KillPotionData.class);
        registerSkill("CLIMB", ClimbData.class);
    }

    public static void registerSkill(String name, Class<? extends SkillData> effectDataClass) {
        skillToData.put(name, effectDataClass);
    }

    public static String getName(Class<? extends SkillData> clazz) { return skillToData.inverse().get(clazz); }

    public static Class<? extends SkillData> getClass(String name) { return skillToData.getOrDefault(name, SkillData.class); }

    public static Collection<Map.Entry<String, Class<? extends SkillData>>> getSkillDataClasses() {
        return skillToData.entrySet();
    }

    public static NamedType[] getNamedTypes() {
        return skillToData.entrySet().stream().map((entry) -> new NamedType(entry.getValue(), entry.getKey())).toArray(NamedType[]::new);
    }
}
