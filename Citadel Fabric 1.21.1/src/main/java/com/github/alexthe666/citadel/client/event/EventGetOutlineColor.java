package com.github.alexthe666.citadel.client.event;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventGetOutlineColor {
    public enum Result { DEFAULT, ALLOW, DENY }
    private static final List<Consumer<EventGetOutlineColor>> LISTENERS = new ArrayList<>();
    public static void addListener(Consumer<EventGetOutlineColor> listener) { LISTENERS.add(listener); }
    public static void post(EventGetOutlineColor event) { for (Consumer<EventGetOutlineColor> l : LISTENERS) l.accept(event); }

    private Entity entityIn;
    private int color;
    private Result result = Result.DEFAULT;

    public EventGetOutlineColor(Entity entityIn, int color) {
        this.entityIn = entityIn;
        this.color = color;
    }

    public Entity getEntityIn() { return entityIn; }
    public void setEntityIn(Entity entityIn) { this.entityIn = entityIn; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
}
