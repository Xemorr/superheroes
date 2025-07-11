package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.skills.ConditionListWrapper;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "skill", visible = true)
public abstract class SkillData {

    @JsonPropertyWithDefault
    private ConditionListWrapper conditions = new ConditionListWrapper();
    @JsonPropertyWithDefault
    private String skill;
    @JsonPropertyWithDefault
    @JsonAlias("respect_protection_plugins")
    private boolean respectProtectionPlugins = true;

    public CompletableFuture<Boolean> areConditionsTrue(Player player, Object... objects) {
        if (conditions.getConditionListIfSkillsLibraryPresent().isEmpty()) return CompletableFuture.completedFuture(true);
        else return conditions.getConditionListIfSkillsLibraryPresent().orElseThrow().ANDConditions(player, false, objects);
    }

    public void ifConditionsTrue(Runnable runnable, Player player, Object... objects) {
        if (conditions == null) runnable.run();
        areConditionsTrue(player, objects).thenAccept((b) -> {
            if (b) runnable.run();
        });
    }

    public ConditionListWrapper getConditions() {
        if (!Superheroes.getInstance().hasSkillsLibrary()) {
            Superheroes.getInstance().getLogger().warning("PLEASE REPORT THIS MESSAGE TO XEMOR IN THE DISCORD! getConditions() called without SkillsLibrary");
        }
        return conditions;
    }

    public boolean shouldRespectProtectionPlugins() {
        return respectProtectionPlugins;
    }

    public String getSkill() {
        return skill;
    }

}
