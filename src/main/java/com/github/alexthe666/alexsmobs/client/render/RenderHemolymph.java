package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.entity.EntityHemolymph;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderHemolymph extends EntityRenderer<EntityHemolymph, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/hemolymph.png");

    public RenderHemolymph(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack p_225623_4_, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityHemolymph p_225623_1_)) {
            return;
        }
        float p_225623_3_ = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        p_225623_4_.pushPose();
        p_225623_4_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_225623_3_, p_225623_1_.yRotO, p_225623_1_.getYRot()) - 90.0F));
        p_225623_4_.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(p_225623_3_, p_225623_1_.xRotO, p_225623_1_.getXRot())));
        float lvt_17_1_ = 0;
        if (lvt_17_1_ > 0.0F) {
            float lvt_18_1_ = -Mth.sin(lvt_17_1_ * 3.0F) * lvt_17_1_;
            p_225623_4_.mulPose(Axis.ZP.rotationDegrees(lvt_18_1_));
        }

        p_225623_4_.mulPose(Axis.XP.rotationDegrees(45.0F));
        p_225623_4_.scale(0.05625F, 0.05625F, 0.05625F);
        p_225623_4_.translate(-4.0D, 0.0D, 0.0D);
        int p_225623_6_ = state.lightCoords;
        collector.submitCustomGeometry(p_225623_4_, RenderTypes.entityCutout(this.getTextureLocation(state)), (pose, lvt_18_2_) -> {
            PoseStack.Pose lvt_19_1_ = p_225623_4_.last();
            Matrix4f lvt_20_1_ = lvt_19_1_.pose();
            Matrix3f lvt_21_1_ = lvt_19_1_.normal();
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, 240);

            for (int lvt_22_1_ = 0; lvt_22_1_ < 4; ++lvt_22_1_) {
                p_225623_4_.mulPose(Axis.XP.rotationDegrees(90.0F));
                this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, 240);
                this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, 240);
                this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, 240);
                this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, 240);
            }
        });

        p_225623_4_.popPose();
        super.submit(state, p_225623_4_, collector, cameraState);
    }

    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, VertexConsumer p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.addVertex(p_229039_1_, (float) p_229039_4_, (float) p_229039_5_, (float) p_229039_6_)
            .setColor(255, 255, 255, 255)
            .setUv(p_229039_7_, p_229039_8_)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(p_229039_12_)
            .setNormal((float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE;
    }
}
