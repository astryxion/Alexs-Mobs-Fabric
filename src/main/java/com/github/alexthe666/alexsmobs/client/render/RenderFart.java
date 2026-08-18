package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelFart;
import com.github.alexthe666.alexsmobs.entity.EntityFart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderFart extends EntityRenderer<EntityFart, EntityRenderState> {
    private static final Identifier FART_TEXTURE = Identifier.parse("alexsmobs:textures/entity/fart.png");
    private static final ModelFart MODEL = new ModelFart();
    private final PoseStack citadelPoseScratch = new PoseStack();

    public RenderFart(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityFart entityIn)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float f = Math.min(entityIn.tickCount + partialTicks, 30F) / 30F;
        float alpha = 1F - f;
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double) 0.15F, 0.0D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        MODEL.setupAnim(entityIn, 0.0F, 0.0F, partialTicks, 0.0F, 0.0F);
        int packedLightIn = state.lightCoords;
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(FART_TEXTURE), (pose, ivertexbuilder) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                MODEL.renderToBuffer(s, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1))
        );
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return FART_TEXTURE;
    }
}
