package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<S extends LivingEntityRenderState> {

    @Shadow
    protected EntityModel<?> model;

    @Inject(
            method = "setupRotations",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel_setupRotations(S state, PoseStack poseStack, float bob, float yBodyRot, CallbackInfo ci) {
        LivingEntity entity = citadel_getLivingEntity(state);
        if (entity != null) {
            EventLivingRenderer.SetupRotations event = new EventLivingRenderer.SetupRotations(entity, model, poseStack, yBodyRot, bob);
            event.post();
        }
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;FFFFF)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel_render_setupAnim_before(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        LivingEntity entity = citadel_getLivingEntity(state);
        if (entity != null) {
            EventLivingRenderer.PreSetupAnimations event = new EventLivingRenderer.PreSetupAnimations(entity, model, poseStack, state.bodyRot, state.ageInTicks, collector, state.lightCoords);
            event.post();
        }
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;FFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel_render_setupAnim_after(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        LivingEntity entity = citadel_getLivingEntity(state);
        if (entity != null) {
            EventLivingRenderer.PostSetupAnimations event = new EventLivingRenderer.PostSetupAnimations(entity, model, poseStack, state.bodyRot, state.ageInTicks, collector, state.lightCoords);
            event.post();
        }
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel_render_renderToBuffer(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        LivingEntity entity = citadel_getLivingEntity(state);
        if (entity != null) {
            EventLivingRenderer.PostRenderModel event = new EventLivingRenderer.PostRenderModel(entity, model, poseStack, state.bodyRot, state.ageInTicks, collector, state.lightCoords);
            event.post();
        }
    }

    private static LivingEntity citadel_getLivingEntity(LivingEntityRenderState state) {
        LivingEntity mapped = EventLivingRenderer.getLiving(state);
        if (mapped != null) {
            return mapped;
        }
        if (Minecraft.getInstance().level == null) {
            return null;
        }
        if (state instanceof net.minecraft.client.renderer.entity.state.AvatarRenderState avatar) {
            Entity entity = Minecraft.getInstance().level.getEntity(avatar.id);
            return entity instanceof LivingEntity livingEntity ? livingEntity : null;
        }
        return null;
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("RETURN")
    )
    private void citadel_extractRenderState(LivingEntity entity, S state, float partialTick, CallbackInfo ci) {
        EventLivingRenderer.mapState(state, entity);
    }
}
