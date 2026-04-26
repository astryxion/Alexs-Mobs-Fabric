package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelBlueJay;
import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class RenderBlueJay extends MobRenderer<EntityBlueJay, LivingEntityRenderState, CitadelEntityModelBridge<EntityBlueJay>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/blue_jay.png");
    private static final Identifier TEXTURE_SHINY = Identifier.parse("alexsmobs:textures/entity/blue_jay_shiny.png");

    public RenderBlueJay(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelBlueJay()), 0.2F);
        this.addLayer(new LayerShiny(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityBlueJay mob = AlexsMobsClientKeys.getLiving(state) instanceof EntityBlueJay b ? b : null;
        if (mob == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
        if (mob.isPassenger() && mob.getVehicle() != null) {
            if (mob.getVehicle() instanceof EntityRaccoon entityRaccoon) {
                if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityRaccoon) instanceof RenderRaccoon raccoonRenderer) {
                    ModelRaccoon raccoonModel = (ModelRaccoon) raccoonRenderer.getModel().citadel();
                    float begProgress = entityRaccoon.prevBegProgress
                            + (entityRaccoon.begProgress - entityRaccoon.prevBegProgress) * partialTicks;
                    float standProgress0 = entityRaccoon.prevStandProgress
                            + (entityRaccoon.standProgress - entityRaccoon.prevStandProgress) * partialTicks;
                    float sitProgress = entityRaccoon.prevSitProgress
                            + (entityRaccoon.sitProgress - entityRaccoon.prevSitProgress) * partialTicks;
                    float standProgress = Math.max(Math.max(begProgress, standProgress0) - sitProgress, 0);
                    matrixStackIn.translate(0F, -1.03F - sitProgress * 0.01F, 0F);
                    Vec3 vec = raccoonModel.getRidingPosition(new Vec3(0, 0, -0.1F + standProgress * 0.1F));
                    matrixStackIn.translate(vec.x, vec.y, vec.z);
                }
            }
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    static class LayerShiny extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityBlueJay>> {

        LayerShiny(RenderBlueJay renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityBlueJay entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityBlueJay j ? j : null;
            if (entitylivingbaseIn == null || entitylivingbaseIn.getFeedTime() <= 0) {
                return;
            }
            float alpha = (float) (1F + Math.sin(state.ageInTicks * 0.3F)) * 0.1F + 0.8F;
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            this.getParentModel().setupAnim(state);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(TEXTURE_SHINY), (pose, consumer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, AMColorUtil.packColor(1.0F, 1.0F, 1.0F, alpha)));
        }
    }
}
