package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.xemor.configurationdata.Duration;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;

public class EraserData extends CooldownData {

    @JsonPropertyWithDefault
    private double range = 30;
    @JsonPropertyWithDefault
    private String removedMessage = "<player> has had their power erased temporarily!";
    @JsonPropertyWithDefault
    private String returnedMessage = "<player> has had their power reinstated!";
    @JsonPropertyWithDefault
    private Duration duration = new Duration(7.5D);

    public double getRange() {
        return range;
    }

    public String getRemovedMessage() {
        return removedMessage;
    }

    public long getDuration() {
        return duration.getDurationInTicks().orElse(150L);
    }

    public String getReturnedMessage() {
        return returnedMessage;
    }
}
