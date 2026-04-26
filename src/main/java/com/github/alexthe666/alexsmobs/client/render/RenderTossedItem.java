package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

public class RenderTossedItem extends EntityRenderer<EntityTossedItem, EntityRenderState> {

    public static final Identifier DART_TEXTURE = Identifier.parse("alexsmobs:textures/entity/ancient_dart.png");
    public static final ModelAncientDart DART_MODEL = new ModelAncientDart();

    public RenderTossedItem(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityTossedItem entityIn)) {
            super.submit(state, matrixStackIn, collector, cameraState);
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        int packedLightIn = state.lightCoords;

        matrixStackIn.pushPose();
        if (entityIn.isDart()) {
            matrixStackIn.translate(0.0D, (double) -0.15F, 0.0D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180F));
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
            matrixStackIn.translate(0, 0.5F, 0);
            matrixStackIn.scale(1F, 1F, 1F);
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(DART_TEXTURE), (pose, ivertexbuilder) ->
                DART_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1)
            );
            matrixStackIn.popPose();
        } else {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
            matrixStackIn.translate(0, 0.5F, 0);
            matrixStackIn.scale(1F, 1F, 1F);
            matrixStackIn.mulPose((new Quaternionf()).rotateZ(Maths.rad(-(entityIn.tickCount + partialTicks) * 30F)));
            matrixStackIn.translate(0, -0.15F, 0);
            ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();
            ItemStackRenderState rs = new ItemStackRenderState();
            resolver.updateForTopItem(rs, entityIn.getItem(), ItemDisplayContext.GROUND, entityIn.level() instanceof ClientLevel cl ? cl : null, null, 0);
            rs.submit(matrixStackIn, collector, packedLightIn, OverlayTexture.NO_OVERLAY, 0);
        }
        matrixStackIn.popPose();

        super.submit(state, matrixStackIn, collector, cameraState);
    }
}
