package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGuster;
import com.github.alexthe666.alexsmobs.entity.EntityGust;
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

public class RenderGust extends EntityRenderer<EntityGust, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/guster.png");
    private final ModelGuster model = new ModelGuster();

    public RenderGust(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityGust entityIn)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double) 0.5F, 0.0D);
        if (!entityIn.getVertical()) {
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180F));
        } else {
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180F));

        }
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        this.model.hideEyes();
        int packedLightIn = state.lightCoords;
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(TEXTURE), (pose, ivertexbuilder) ->
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1)
        );
        this.model.showEyes();
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE;
    }
}
