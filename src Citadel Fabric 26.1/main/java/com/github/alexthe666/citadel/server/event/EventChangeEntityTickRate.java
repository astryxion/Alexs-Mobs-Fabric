package com.github.alexthe666.citadel.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public class EventChangeEntityTickRate {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    if (listener.onChange(event)) {
                        return true;
                    }
                }
                return false;
            });

    private final Entity entity;
    private final float targetTickRate;
    private boolean canceled = false;

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

    public void post() {
        this.canceled = EVENT.invoker().onChange(this);
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    @FunctionalInterface
    public interface Listener {
        boolean onChange(EventChangeEntityTickRate event);
    }
}
