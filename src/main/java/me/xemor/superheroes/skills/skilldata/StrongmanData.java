package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;

public class StrongmanData extends SkillData {

    @JsonPropertyWithDefault
    private double velocity = 2.5;
    @JsonPropertyWithDefault
    @JsonAlias("upwardsvelocity")
    private double upwardsVelocity = 1;
    @JsonPropertyWithDefault
    @JsonAlias("toomuscularmessage")
    private String tooMuscularMessage = "<player> <white> is too strong to sit in a vehicle!";
    @JsonPropertyWithDefault
    @JsonAlias("maxpassengers")
    private int maxPassengers = 10;

    public double getVelocity() {
        return velocity;
    }

    public double getUpwardsVelocity() {
        return upwardsVelocity;
    }

    public String getTooMuscularMessage() {
        return tooMuscularMessage;
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }
}
