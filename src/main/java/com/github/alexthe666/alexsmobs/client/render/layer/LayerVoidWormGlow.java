package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBody;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormTail;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderVoidWormBody;
import com.github.alexthe666.alexsmobs.client.render.misc.VoidWormMetadataSection;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public abstract class LayerVoidWormGlow<M extends EntityModel<LivingEntityRenderState>> extends RenderLayer<LivingEntityRenderState, M> {

    private final ResourceManager resourceManager;
    private final Object2BooleanMap<Identifier> mcmetaData;
    private final ModelVoidWormBody bodyModel = new ModelVoidWormBody(1.001F);
    private final ModelVoidWormTail tailModel = new ModelVoidWormTail(1.001F);
    private final ModelVoidWorm headGlowModel = new ModelVoidWorm(1.001F);

    public LayerVoidWormGlow(RenderLayerParent<LivingEntityRenderState, M> renderer, ResourceManager resourceManager) {
        super(renderer);
        this.resourceManager = resourceManager;
        this.mcmetaData = new Object2BooleanOpenHashMap<>();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        LivingEntity wormLiving = AlexsMobsClientKeys.getLiving(state);
        if (wormLiving == null) {
            return;
        }
        Identifier texture = getGlowTexture(wormLiving);
        boolean special = isSpecialRenderer(texture);
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
        float ageInTicks = state.ageInTicks;
        float netHead = state.yRot;
        float pitch = state.xRot;

        if (!isGlowing(wormLiving) && !special) {
            return;
        }
        if (special) {
            if (wormLiving instanceof EntityVoidWormPart part) {
                AlexAdvancedEntityModel<EntityVoidWormPart> layerActive = part.isTail() ? tailModel : bodyModel;
                layerActive.prepareMobModel(part, limbSwing, limbSwingAmount, ageInTicks);
                layerActive.setupAnim(part, limbSwing, limbSwingAmount, ageInTicks, netHead, pitch);
                collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(texture), (pose, consumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, new PoseStack(), scratch -> layerActive.renderToBuffer(scratch, consumer, 240, OverlayTexture.NO_OVERLAY, -1))
                );
            } else if (wormLiving instanceof EntityVoidWorm headWorm) {
                headGlowModel.prepareMobModel(headWorm, limbSwing, limbSwingAmount, ageInTicks);
                headGlowModel.setupAnim(headWorm, limbSwing, limbSwingAmount, ageInTicks, netHead, pitch);
                collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(texture), (pose, consumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, new PoseStack(), scratch -> headGlowModel.renderToBuffer(scratch, consumer, 240, OverlayTexture.NO_OVERLAY, -1))
                );
            }
        } else {
            getAlpha(wormLiving);
            M pm = getParentModel();
            if (pm instanceof CitadelEntityModelBridge<?> bridge) {
                @SuppressWarnings("unchecked")
                AdvancedEntityModel<LivingEntity> adv = (AdvancedEntityModel<LivingEntity>) bridge.citadel();
                adv.prepareMobModel(wormLiving, limbSwing, limbSwingAmount, ageInTicks);
                adv.setupAnim(wormLiving, limbSwing, limbSwingAmount, ageInTicks, netHead, pitch);
                collector.submitCustomGeometry(poseStack, RenderTypes.eyes(texture), (pose, consumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, new PoseStack(), scratch -> adv.renderToBuffer(scratch, consumer, 240, LivingEntityRenderer.getOverlayCoords(state, 1.0F), -1))
                );
            } else {
                AlexAdvancedEntityModel<EntityVoidWormPart> mesh = RenderVoidWormBody.eyesMeshForLayer(wormLiving, pm);
                if (mesh != null && wormLiving instanceof EntityVoidWormPart part) {
                    mesh.prepareMobModel(part, limbSwing, limbSwingAmount, ageInTicks);
                    mesh.setupAnim(part, limbSwing, limbSwingAmount, ageInTicks, netHead, pitch);
                    collector.submitCustomGeometry(poseStack, RenderTypes.eyes(texture), (pose, consumer) ->
                        AlexAdvancedEntityModel.withCitadelSubmitPose(pose, new PoseStack(), scratch -> mesh.renderToBuffer(scratch, consumer, 240, LivingEntityRenderer.getOverlayCoords(state, 1.0F), -1))
                    );
                }
            }
        }
    }

    public abstract Identifier getGlowTexture(LivingEntity worm);

    public abstract boolean isGlowing(LivingEntity livingEntity);

    public abstract float getAlpha(LivingEntity livingEntity);

    private boolean isSpecialRenderer(Identifier id) {
        if (mcmetaData.containsKey(id)) {
            return mcmetaData.getBoolean(id);
        }
        if (this.resourceManager.getResource(id).isPresent()) {
            Resource resource = this.resourceManager.getResource(id).get();
            try {
                VoidWormMetadataSection section = resource.metadata().getSection(VoidWormMetadataSection.TYPE).orElse(new VoidWormMetadataSection(false));
                mcmetaData.put(id, section.isEndPortalTexture());
                return section.isEndPortalTexture();
            } catch (Exception e) {
                e.printStackTrace();
                mcmetaData.put(id, false);
                return false;
            }
        }
        mcmetaData.put(id, false);
        return false;
    }
}
