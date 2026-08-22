package com.github.alexthe666.citadel.client.event;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventGetFluidRenderType {
    public enum Result { DEFAULT, ALLOW, DENY }
    private static final List<Consumer<EventGetFluidRenderType>> LISTENERS = new ArrayList<>();
    public static void addListener(Consumer<EventGetFluidRenderType> listener) { LISTENERS.add(listener); }
    public static void post(EventGetFluidRenderType event) { for (Consumer<EventGetFluidRenderType> l : LISTENERS) l.accept(event); }

    private final FluidState fluidState;
    private RenderType renderType;
    private Result result = Result.DEFAULT;

    public EventGetFluidRenderType(FluidState fluidState, RenderType renderType) {
        this.fluidState = fluidState;
        this.renderType = renderType;
    }

    public FluidState getFluidState() { return fluidState; }
    public RenderType getRenderType() { return renderType; }
    public void setRenderType(RenderType renderType) { this.renderType = renderType; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
}
