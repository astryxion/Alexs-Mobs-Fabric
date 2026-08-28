package com.github.alexthe666.citadel.server.event;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventChangeEntityTickRate {
    private final Entity entity;
    private final float targetTickRate;
    private boolean canceled;

    private static final List<Consumer<EventChangeEntityTickRate>> LISTENERS = new ArrayList<>();

    public static void addListener(Consumer<EventChangeEntityTickRate> listener) {
        LISTENERS.add(listener);
    }

    public static void post(EventChangeEntityTickRate event) {
        for (Consumer<EventChangeEntityTickRate> listener : LISTENERS) {
            listener.accept(event);
        }
    }

    public EventChangeEntityTickRate(Entity entity, float targetTickRate) {
        this.entity = entity;
        this.targetTickRate = targetTickRate;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getTargetTickRate() {
        return targetTickRate;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
