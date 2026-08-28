package com.github.alexthe666.citadel.client.event;

import net.minecraft.client.multiplayer.ClientLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventGetStarBrightness {
    public enum Result { DEFAULT, ALLOW, DENY }
    private static final List<Consumer<EventGetStarBrightness>> LISTENERS = new ArrayList<>();
    public static void addListener(Consumer<EventGetStarBrightness> listener) { LISTENERS.add(listener); }
    public static void post(EventGetStarBrightness event) { for (Consumer<EventGetStarBrightness> l : LISTENERS) l.accept(event); }

    private final ClientLevel clientLevel;
    private float brightness;
    private final float partialTicks;
    private Result result = Result.DEFAULT;

    public EventGetStarBrightness(ClientLevel clientLevel, float brightness, float partialTicks) {
        this.clientLevel = clientLevel;
        this.brightness = brightness;
        this.partialTicks = partialTicks;
    }

    public ClientLevel getLevel() { return clientLevel; }
    public float getPartialTicks() { return partialTicks; }
    public float getBrightness() { return brightness; }
    public void setBrightness(float brightness) { this.brightness = brightness; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
}
