package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherStateCaptureMixin {

    @Inject(method = "extractEntity", at = @At("RETURN"))
    private void alexsmobs$captureEntityForState(Entity entity, float partialTicks, CallbackInfoReturnable<EntityRenderState> cir) {
        EntityRenderState state = cir.getReturnValue();
        AlexsMobsClientKeys.setEntity(state, entity);
    }

    /**
     * Capture the current world-render camera so {@link ClientProxy#submitEntityInWorld} can render entities manually
     * (pouch babies, whale-captured squid). Without this, {@code lastCameraRenderState} stays null and every manual
     * render silently no-ops, so those entities vanish entirely. Runs at HEAD of every entity submit, so the camera is
     * already set before any render layer fires.
     */
    @Inject(method = "submit", at = @At("HEAD"))
    private void alexsmobs$captureCameraForState(EntityRenderState state, CameraRenderState camera, double x, double y, double z, PoseStack poseStack, SubmitNodeCollector collector, CallbackInfo ci) {
        ClientProxy.lastCameraRenderState = camera;
    }
}
