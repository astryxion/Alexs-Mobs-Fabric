package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFogEnvironment.class)
public class LavaFogEnvironmentMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private void alexsmobs$lavaVision(FogData fog, Camera camera, ClientLevel level, float renderDistance, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (camera.entity() instanceof LivingEntity living && living.hasEffect(AMEffectRegistry.holder(AMEffectRegistry.LAVA_VISION))) {
            fog.environmentalStart = -8.0F;
            fog.environmentalEnd = 50.0F;
            fog.skyEnd = fog.environmentalEnd;
            fog.cloudEnd = fog.environmentalEnd;
        }
    }
}
