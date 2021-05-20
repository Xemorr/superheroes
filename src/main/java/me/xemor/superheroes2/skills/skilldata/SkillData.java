package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.Spell.SpellData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public abstract class SkillData {

    private final Skill skill;
    private ConfigurationSection configurationSection;

    protected SkillData(Skill skill, ConfigurationSection configurationSection) {
        this.skill = skill;
        this.configurationSection = configurationSection;
    }

    @Nullable
    public static SkillData create(Skill skill, ConfigurationSection configurationSection) {
        switch (skill) {
            case INSTANTBREAK: return new InstantBreakData(skill, configurationSection);
            case POTIONEFFECT:
            case SNEAKINGPOTION: return new PotionEffectSkillData(skill, configurationSection);
            case LIGHT: return new LightSkillData(skill, configurationSection);
            case NOHUNGER: return new BlankData(skill, configurationSection);
            case DAMAGERESISTANCE: return new DamageResistanceData(skill, configurationSection);
            case SLIME: return new SlimeData(skill, configurationSection);
            case EGGLAYER: return new EggLayerData(skill, configurationSection);
            case AURA: return new AuraData(skill, configurationSection);
            case WALKER: return new WalkerData(skill, configurationSection);
            case PICKPOCKET: return new PickpocketData(skill, configurationSection);
            case STRONGMAN: return new StrongmanData(skill, configurationSection);
            case PHASE: return new PhaseData(skill, configurationSection);
            case SLAM: return new SlamData(skill, configurationSection);
            case ERASER: return new EraserData(skill, configurationSection);
            case CRAFTING: return new CraftingData(skill, configurationSection);
            case TELEPORT: return new TeleportData(skill, configurationSection);
            case SUMMON: return new SummonSkillData(skill, configurationSection);
            case DECOY: return new DecoyData(skill, configurationSection);
            case POTIONGIFTER: return new PotionGifterSkillData(skill, configurationSection);
            case CONSUME: return new ConsumeSkillData(skill, configurationSection);
            case BLOCKRAY: return new BlockRayData(skill, configurationSection);
            case OHKO: return new OHKOData(skill, configurationSection);
            case REPULSION: return new RepulsionData(skill, configurationSection);
            case CREEPER: return new CreeperData(skill, configurationSection);
            case GIVEITEM: return new GiveItemData(skill, configurationSection);
            case GUN: return new GunData(skill, configurationSection);
            case SNEAK: return new SneakData(skill, configurationSection);
            case SHIELD: return new ShieldData(skill, configurationSection);
            case SPELL: return new SpellData(skill, configurationSection);
            case THROWER: return new ThrowerData(skill, configurationSection);
            case CONVERTITEM: return new ConvertItemData(skill, configurationSection);
            case CONVERTBLOCK: return new ConvertBlockData(skill, configurationSection);
            case REMOTEDETONATION: return new RemoteDetonationData(skill, configurationSection);
            case BLOCKDROPS: return new BlockDropsData(skill, configurationSection);
            default:
                return null;
        }
    }

    public ConfigurationSection getData() {
        return configurationSection;
    }

    public Skill getSkill() {
        return skill;
    }

}
