package com.github.alexthe666.citadel.animation;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnimationEvent<T extends Entity & IAnimatedEntity> {
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
        private static final List<Consumer<Start<?>>> LISTENERS = new ArrayList<>();
        private boolean canceled;

        public static void addListener(Consumer<Start<?>> listener) {
            LISTENERS.add(listener);
        }

        public static boolean post(Start<?> event) {
            for (Consumer<Start<?>> listener : LISTENERS) {
                listener.accept(event);
            }
            return event.canceled;
        }

        public Start(T entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }

    public static class Tick<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        private static final List<Consumer<Tick<?>>> LISTENERS = new ArrayList<>();
        protected int tick;

        public static void addListener(Consumer<Tick<?>> listener) {
            LISTENERS.add(listener);
        }

        public static void post(Tick<?> event) {
            for (Consumer<Tick<?>> listener : LISTENERS) {
                listener.accept(event);
            }
        }

        public Tick(T entity, Animation animation, int tick) {
            super(entity, animation);
            this.tick = tick;
        }

        public int getTick() {
            return this.tick;
        }
    }
}
