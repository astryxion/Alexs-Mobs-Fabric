package com.github.alexthe666.citadel.client.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventPosePlayerHand {
    public enum Result { DEFAULT, ALLOW, DENY }
    private static final List<Consumer<EventPosePlayerHand>> LISTENERS = new ArrayList<>();
    public static void addListener(Consumer<EventPosePlayerHand> listener) { LISTENERS.add(listener); }
    public static void post(EventPosePlayerHand event) { for (Consumer<EventPosePlayerHand> l : LISTENERS) l.accept(event); }

    private final LivingEntity entityIn;
    private final HumanoidModel model;
    private final boolean left;
    private Result result = Result.DEFAULT;

    public EventPosePlayerHand(LivingEntity entityIn, HumanoidModel model, boolean left) {
        this.entityIn = entityIn;
        this.model = model;
        this.left = left;
    }

    public Entity getEntityIn() { return entityIn; }
    public HumanoidModel getModel() { return model; }
    public boolean isLeftHand() { return left; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
}
