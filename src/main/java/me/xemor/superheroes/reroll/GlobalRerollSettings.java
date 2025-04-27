package me.xemor.superheroes.reroll;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class GlobalRerollSettings {

    @JsonPropertyWithDefault
    private boolean itemEnabled = true;
    @JsonPropertyWithDefault
    private boolean eachHeroRequiresPermission;
    @JsonPropertyWithDefault
    private double itemCooldown;

    public boolean isItemEnabled() {
        return itemEnabled;
    }

    public boolean doesEachHeroRequirePermissions() {
        return eachHeroRequiresPermission;
    }

    public double getItemCooldown() {
        return itemCooldown;
    }
}
