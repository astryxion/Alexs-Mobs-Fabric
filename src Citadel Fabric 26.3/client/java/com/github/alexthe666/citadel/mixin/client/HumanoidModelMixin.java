package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Inject(
            at = @At("HEAD"),
            remap = CitadelConstants.REMAPREFS,
            method = "translateToHand(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            cancellable = true
    )
    private void citadel_translateToHand(HumanoidRenderState state, HumanoidArm arm, PoseStack poseStack, CallbackInfo ci) {
        LivingEntity entity = null;
        if (state instanceof AvatarRenderState avatar) {
            if (Minecraft.getInstance().level != null) {
                Entity e = Minecraft.getInstance().level.getEntity(avatar.id);
                if (e instanceof LivingEntity le) {
                    entity = le;
                }
            }
        }
        EventPosePlayerHand event = new EventPosePlayerHand(entity, (HumanoidModel<?>) (Object) this, arm == HumanoidArm.LEFT);
        event.post();
        if (event.getResult() == TriState.TRUE) {
            ci.cancel();
        }
    }
}
