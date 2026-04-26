package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;

public final class CitadelEntityModelBridge<E extends LivingEntity> extends EntityModel<LivingEntityRenderState> {

    private final AdvancedEntityModel<E> citadel;

    public CitadelEntityModelBridge(AdvancedEntityModel<E> citadel) {
        super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
        this.citadel = citadel;
    }

    public AdvancedEntityModel<E> citadel() {
        return citadel;
    }

    /** Citadel mesh draw for layers and custom submit (same as {@link #renderToBuffer(PoseStack, VertexConsumer, int, int, int)}). */
    public void renderCitadelToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.citadel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
    }

    /**
     * Sets {@link AlexAdvancedEntityModel#young} on the wrapped Citadel model (replaces removed {@code EntityModel#young}).
     */
    public void setCitadelYoung(boolean young) {
        ((AlexAdvancedEntityModel<?>) this.citadel).young = young;
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        LivingEntity raw = AlexsMobsClientKeys.getLiving(state);
        if (raw == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        E entity = (E) raw;
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
        float ageInTicks = state.ageInTicks;
        // Vanilla LivingEntityRenderer.extractRenderState: state.yRot is already head yaw relative to body (wrapped).
        float netHeadYaw = state.yRot;
        float headPitch = state.xRot;
        citadel.prepareMobModel(entity, limbSwing, limbSwingAmount, ageInTicks);
        citadel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void copyPropertiesTo(HumanoidModel<?> model) {
        if (citadel instanceof ModelKangaroo kangaroo) {
            kangaroo.copyArmorPoseToHumanoid(model);
        }
    }
}