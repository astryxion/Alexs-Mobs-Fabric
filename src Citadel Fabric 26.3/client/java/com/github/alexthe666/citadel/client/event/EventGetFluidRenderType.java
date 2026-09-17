package com.github.alexthe666.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.util.TriState;

/** Client-only event; lives under {@code client.event} and is only referenced from client code. */
public class EventGetFluidRenderType {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    listener.onEvent(event);
                }
            });
    private FluidState fluidState;
    private RenderType renderType;
    private TriState result = TriState.DEFAULT;

    public EventGetFluidRenderType(FluidState fluidState, RenderType renderType) {
        this.fluidState = fluidState;
        this.renderType = renderType;
    }

    public FluidState getFluidState() {
        return fluidState;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
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
        void onEvent(EventGetFluidRenderType event);
    }
}
