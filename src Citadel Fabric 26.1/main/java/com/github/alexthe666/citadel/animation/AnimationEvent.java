package com.github.alexthe666.citadel.animation;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public class AnimationEvent<T extends Entity & IAnimatedEntity> {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final Event<StartListener> START_EVENT = EventFactory.createArrayBacked(
            StartListener.class,
            listeners -> event -> {
                for (StartListener listener : listeners) {
                    if (listener.onStart(event)) {
                        return true;
                    }
                }
                return false;
            });
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final Event<TickListener> TICK_EVENT = EventFactory.createArrayBacked(
            TickListener.class,
            listeners -> event -> {
                for (TickListener listener : listeners) {
                    listener.onTick(event);
                }
            });

    protected Animation animation;
    private final T entity;

    AnimationEvent(T entity, Animation animation) {
        this.entity = entity;
        this.animation = animation;
    }

    public T getEntity() {
        return this.entity;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public static class Start<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        private boolean canceled = false;

        public Start(T entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }

        public void post() {
            this.canceled = START_EVENT.invoker().onStart(this);
        }

        public boolean isCanceled() {
            return this.canceled;
        }
    }

    public static class Tick<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        protected int tick;

        public Tick(T entity, Animation animation, int tick) {
            super(entity, animation);
            this.tick = tick;
        }

        public int getTick() {
            return this.tick;
        }

        public void post() {
            TICK_EVENT.invoker().onTick(this);
        }
    }

    @FunctionalInterface
    public interface StartListener<T extends Entity & IAnimatedEntity> {
        boolean onStart(Start<T> event);
    }

    @FunctionalInterface
    public interface TickListener<T extends Entity & IAnimatedEntity> {
        void onTick(Tick<T> event);
    }
}