package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderCachalotEcho extends EntityRenderer<EntityCachalotEcho, EntityRenderState> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_2.png");
    private static final Identifier TEXTURE_3 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_3.png");
    private static final Identifier GREEN_TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_0_green.png");
    private static final Identifier GREEN_TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_1_green.png");
    private static final Identifier GREEN_TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_2_green.png");
    private static final Identifier GREEN_TEXTURE_3 = Identifier.parse("alexsmobs:textures/entity/cachalot/whale_echo_3_green.png");

    public RenderCachalotEcho(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityCachalotEcho entityIn)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, 0.25F, 0.0D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        int arcs = Mth.clamp(Mth.floor(entityIn.tickCount / 5F), 1, 4);
        matrixStackIn.translate(0.0D, 0.0F, 0.4D);
        for (int i = 0; i < arcs; i++) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, 0, -0.5F * i);
            renderArc(matrixStackIn, collector, (i + 1) * 5, entityIn.isFasterAnimation(), entityIn.isGreen());
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
    }

    private void renderArc(PoseStack matrixStackIn, SubmitNodeCollector collector, int age, boolean fast, boolean green) {
        matrixStackIn.pushPose();
        Identifier res;
        if (fast) {
            res = getEntityTextureFaster(age, green);
        } else {
            res = getEntityTexture(age);
        }
        collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(res), (pose, ivertexbuilder) -> {
            PoseStack.Pose lvt_19_1_ = matrixStackIn.last();
            Matrix4f lvt_20_1_ = lvt_19_1_.pose();
            Matrix3f lvt_21_1_ = lvt_19_1_.normal();
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0, 0, 1, 0, 1, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0, 1, 1, 0, 1, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1, 1, 1, 0, 1, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1, 0, 1, 0, 1, 240);
        });
        matrixStackIn.popPose();
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE_0;
    }

    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, VertexConsumer p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.addVertex(p_229039_1_, (float) p_229039_4_, (float) p_229039_5_, (float) p_229039_6_)
            .setColor(255, 255, 255, 255)
            .setUv(p_229039_7_, p_229039_8_)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(p_229039_12_)
            .setNormal((float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public Identifier getEntityTexture(int age) {
        if (age < 5) {
            return TEXTURE_0;
        } else if (age < 10) {
            return TEXTURE_1;
        } else if (age < 15) {
            return TEXTURE_2;
        } else {
            return TEXTURE_3;
        }
    }

    public Identifier getEntityTextureFaster(int age, boolean green) {
        if (age < 3) {
            return green ? GREEN_TEXTURE_0 : TEXTURE_0;
        } else if (age < 6) {
            return green ? GREEN_TEXTURE_1 : TEXTURE_1;
        } else if (age < 9) {
            return green ? GREEN_TEXTURE_2 : TEXTURE_2;
        } else {
            return green ? GREEN_TEXTURE_3 : TEXTURE_3;
        }
    }
}
