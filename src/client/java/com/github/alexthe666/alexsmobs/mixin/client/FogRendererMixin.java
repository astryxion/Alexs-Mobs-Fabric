package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectPowerDown;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void alexsmobs$customFog(Camera camera, FogRenderer.FogMode fogMode, float viewDistance, boolean thickFog, float partialTick, CallbackInfo ci) {
        if (AMConfig.shadersCompat) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        if (camera.getFluidInCamera() == FogType.LAVA && minecraft.player.hasEffect(AMEffectRegistry.LAVA_VISION)) {
            RenderSystem.setShaderFogStart(-8.0F);
            RenderSystem.setShaderFogEnd(50.0F);
            return;
        }
        if (camera.getFluidInCamera() == FogType.NONE && minecraft.player.hasEffect(AMEffectRegistry.POWER_DOWN)) {
            MobEffectInstance instance = minecraft.player.getEffect(AMEffectRegistry.POWER_DOWN);
            if (instance != null && instance.getEffect() instanceof EffectPowerDown powerDown) {
                float initEnd = RenderSystem.getShaderFogEnd();
                int duration = instance.getDuration();
                float tickDelta = minecraft.getFrameTime();
                float f = Math.min(20, Math.min(powerDown.getActiveTime() + tickDelta, duration + tickDelta)) * 0.05F;
                RenderSystem.setShaderFogStart(-8.0F);
                RenderSystem.setShaderFogEnd(8.0F + (1 - f) * Math.max(0, initEnd - 8.0F));
            }
        }
    }
}
