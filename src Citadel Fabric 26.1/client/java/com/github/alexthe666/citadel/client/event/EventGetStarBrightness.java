package com.github.alexthe666.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.TriState;

public class EventGetStarBrightness {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    listener.onEvent(event);
                }
            });
    private ClientLevel clientLevel;
    private float brightness;
    private float partialTicks;
    private TriState result = TriState.DEFAULT;

    public EventGetStarBrightness(ClientLevel clientLevel, float brightness, float partialTicks) {
        this.clientLevel = clientLevel;
        this.brightness = brightness;
        this.partialTicks = partialTicks;
    }

    public ClientLevel getLevel() {
        return clientLevel;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setResult(TriState result) {
        this.result = result;
    }

    public TriState getResult() {
        return result;
    }

    public void post() {
        EVENT.invoker().onEvent(this);
    }

    @FunctionalInterface
    public interface Listener {
        void onEvent(EventGetStarBrightness event);
    }
}
