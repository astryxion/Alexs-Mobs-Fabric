package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import com.github.alexthe666.citadel.Citadel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Shadow
    protected EntityModel model;

    @Inject(
            method = "setupRotations",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel_setupRotations(LivingEntity livingEntity, PoseStack poseStack, float ageInTicks, float netHeadYaw, float partialTick, CallbackInfo ci) {
        EventLivingRenderer.SetupRotations event = new EventLivingRenderer.SetupRotations(livingEntity, model, poseStack, netHeadYaw, partialTick);
        EventLivingRenderer.SetupRotations.post(event);

    }

    @Inject(
            method = "render",
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel_render_setupAnim_before(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        EventLivingRenderer.PreSetupAnimations event = new EventLivingRenderer.PreSetupAnimations(livingEntity, model, poseStack, yaw, partialTicks, bufferSource, packedLight);
        EventLivingRenderer.PreSetupAnimations.post(event);

    }

    @Inject(
            method = "render",
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel_render_setupAnim_after(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        EventLivingRenderer.PostSetupAnimations event = new EventLivingRenderer.PostSetupAnimations(livingEntity, model, poseStack, yaw, partialTicks, bufferSource, packedLight);
        EventLivingRenderer.PostSetupAnimations.post(event);
    }

    @Inject(
            method = "render",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel_render_renderToBuffer(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        EventLivingRenderer.PostRenderModel event = new EventLivingRenderer.PostRenderModel(livingEntity, model, poseStack, yaw, partialTicks, bufferSource, packedLight);
        EventLivingRenderer.PostRenderModel.post(event);
        if (livingEntity instanceof Player player && Citadel.PROXY instanceof com.github.alexthe666.citadel.ClientProxy) {
            com.github.alexthe666.citadel.ClientProxy.afterRenderPlayer(poseStack, bufferSource, packedLight, partialTicks, player);
        }
    }
}
