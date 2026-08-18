package com.github.alexthe666.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.TriState;

import javax.annotation.Nullable;

public class EventPosePlayerHand {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    listener.onEvent(event);
                }
            });
    private final @Nullable LivingEntity entityIn;
    private final HumanoidModel<?> model;
    private final boolean left;
    private TriState result = TriState.DEFAULT;

    public EventPosePlayerHand(@Nullable LivingEntity entityIn, HumanoidModel<?> model, boolean left) {
        this.entityIn = entityIn;
        this.model = model;
        this.left = left;
    }

    public @Nullable LivingEntity getEntityIn() {
        return entityIn;
    }

    public HumanoidModel<?> getModel() {
        return model;
    }

    public boolean isLeftHand() {
        return left;
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
        void onEvent(EventPosePlayerHand event);
    }
}
