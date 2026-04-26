package com.github.alexthe666.citadel.client.shader;

import com.github.alexthe666.citadel.Citadel;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostEffectRegistry {

    private static List<Identifier> registry = new ArrayList<>();

    private static Map<Identifier, PostEffect> postEffects = new HashMap<>();

    public static void clear() {
        for (PostEffect postEffect : postEffects.values()) {
            postEffect.close();
        }
        postEffects.clear();
    }

    public static void registerEffect(Identifier id) {
        registry.add(id);
    }

    public static void onInitializeOutline() {
        clear();
        Minecraft minecraft = Minecraft.getInstance();
        int w = minecraft.getWindow().getWidth();
        int h = minecraft.getWindow().getHeight();
        for (Identifier id : registry) {
            PostChain postChain = minecraft.getShaderManager().getPostChain(id, LevelTargetBundle.MAIN_TARGETS);
            TextureTarget renderTarget = new TextureTarget("Citadel post-effect " + id, w, h, false);
            if (postChain == null) {
                Citadel.LOGGER.warn("Failed to load post chain: {}", id);
                renderTarget.destroyBuffers();
                postEffects.put(id, new PostEffect(null, null, false));
            } else {
                postEffects.put(id, new PostEffect(postChain, renderTarget, false));
            }
        }
    }

    public static void resize(int x, int y) {
        for (PostEffect postEffect : postEffects.values()) {
            postEffect.resize(x, y);
        }
    }

    public static RenderTarget getRenderTargetFor(Identifier id) {
        PostEffect effect = postEffects.get(id);
        return effect == null ? null : effect.getRenderTarget();
    }

    public static void renderEffectForNextTick(Identifier id) {
        PostEffect effect = postEffects.get(id);
        if (effect != null) {
            effect.setEnabled(true);
        }
    }

    private static void clearColor(RenderTarget target) {
        if (target != null && target.getColorTexture() != null) {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(target.getColorTexture(), 0);
        }
    }

    public static void blitEffects() {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        for (PostEffect postEffect : postEffects.values()) {
            if (postEffect.postChain != null && postEffect.isEnabled()) {
                RenderTarget effectTarget = postEffect.getRenderTarget();
                if (effectTarget != null && mainTarget.getColorTextureView() != null) {
                    effectTarget.blitAndBlendToTexture(mainTarget.getColorTextureView());
                    clearColor(effectTarget);
                }
                postEffect.setEnabled(false);
            }
        }
    }

    public static void clearAndBindWrite(RenderTarget mainTarget) {
        for (PostEffect postEffect : postEffects.values()) {
            if (postEffect.isEnabled() && postEffect.postChain != null) {
                clearColor(postEffect.getRenderTarget());
            }
        }
    }

    public static void processEffects(RenderTarget mainTarget) {
        for (PostEffect postEffect : postEffects.values()) {
            if (postEffect.isEnabled() && postEffect.postChain != null) {
                RenderTarget effectTarget = postEffect.getRenderTarget();
                if (effectTarget != null) {
                    postEffect.postChain.process(effectTarget, GraphicsResourceAllocator.UNPOOLED);
                }
            }
        }
    }

    private static class PostEffect {
        private PostChain postChain;
        private RenderTarget renderTarget;
        private boolean enabled;

        PostEffect(PostChain postChain, RenderTarget renderTarget, boolean enabled) {
            this.postChain = postChain;
            this.renderTarget = renderTarget;
            this.enabled = enabled;
        }

        PostChain getPostChain() {
            return postChain;
        }

        RenderTarget getRenderTarget() {
            return renderTarget;
        }

        boolean isEnabled() {
            return enabled;
        }

        void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        void close() {
            if (renderTarget != null) {
                renderTarget.destroyBuffers();
            }
        }

        void resize(int x, int y) {
            if (renderTarget != null) {
                renderTarget.resize(x, y);
            }
        }
    }
}
