package com.github.alexthe666.citadel.client.event;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventRenderSplashText {
    private String splashText;

    private final GuiGraphics guiGraphics;
    private final float partialTicks;

    public EventRenderSplashText(String splashText, GuiGraphics guiGraphics, float partialTicks) {
        this.splashText = splashText;
        this.guiGraphics = guiGraphics;
        this.partialTicks = partialTicks;
    }

    public String getSplashText() {
        return splashText;
    }

    public void setSplashText(String splashText) {
        this.splashText = splashText;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public enum Result {
        DEFAULT,
        ALLOW,
        DENY
    }

    public static class Pre extends EventRenderSplashText {
        private static final List<Consumer<Pre>> LISTENERS = new ArrayList<>();
        private int splashTextColor;
        private Result result = Result.DEFAULT;

        public static void addPreListener(Consumer<Pre> listener) {
            LISTENERS.add(listener);
        }

        public static void invokePre(Pre event) {
            for (Consumer<Pre> listener : LISTENERS) {
                listener.accept(event);
            }
        }

        public Pre(String splashText, GuiGraphics guiGraphics, float partialTicks, int splashTextColor) {
            super(splashText, guiGraphics, partialTicks);
            this.splashTextColor = splashTextColor;
        }

        public int getSplashTextColor() {
            return splashTextColor;
        }

        public void setSplashTextColor(int splashTextColor) {
            this.splashTextColor = splashTextColor;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }
    }

    public static class Post extends EventRenderSplashText {
        private static final List<Consumer<Post>> LISTENERS = new ArrayList<>();
        public static void addPostListener(Consumer<Post> listener) { LISTENERS.add(listener); }
        public static void invokePost(Post event) { for (Consumer<Post> l : LISTENERS) l.accept(event); }

        public Post(String splashText, GuiGraphics guiGraphics, float partialTicks) {
            super(splashText, guiGraphics, partialTicks);
        }
    }
}
