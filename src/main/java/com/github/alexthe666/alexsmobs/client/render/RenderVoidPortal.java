package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

public class RenderVoidPortal extends EntityRenderer<EntityVoidPortal, EntityRenderState> {

    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/portal_idle_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/portal_idle_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/portal_idle_2.png");
    private static final Identifier TEXTURE_SHATTERED_0 = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/shattered/portal_idle_0.png");
    private static final Identifier TEXTURE_SHATTERED_1 = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/shattered/portal_idle_1.png");
    private static final Identifier TEXTURE_SHATTERED_2 = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/shattered/portal_idle_2.png");
    private static final Identifier[] TEXTURE_PROGRESS = new Identifier[10];
    private static final Identifier[] TEXTURE_SHATTERED_PROGRESS = new Identifier[10];

    public RenderVoidPortal(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        for (int i = 0; i < 10; i++) {
            TEXTURE_PROGRESS[i] = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/portal_grow_" + i + ".png");
            TEXTURE_SHATTERED_PROGRESS[i] = Identifier.parse("alexsmobs:textures/entity/void_worm/portal/shattered/portal_grow_" + i + ".png");
        }
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    private void renderPortal(EntityVoidPortal entityIn, PoseStack matrixStackIn, SubmitNodeCollector collector, boolean shattered) {
        Identifier tex;
        if (entityIn.getLifespan() < 20) {
            tex = getGrowingTexture((int) ((entityIn.getLifespan() * 0.5F) % 10), shattered);
        } else if (entityIn.tickCount < 20) {
            tex = getGrowingTexture((int) ((entityIn.tickCount * 0.5F) % 10), shattered);
        } else {
            tex = getIdleTexture(entityIn.tickCount % 9, shattered);
        }
        net.minecraft.client.renderer.rendertype.RenderType renderType = shattered ? RenderTypes.entityTranslucentEmissive(tex) : AMRenderTypes.getFullBright(tex);
        // submitCustomGeometry replays with the captured PoseStack.Pose; the live matrixStackIn is often popped before replay (wrong transform / invisible).
        collector.submitCustomGeometry(matrixStackIn, renderType, (pose, ivertexbuilder) -> renderArc(pose, ivertexbuilder));
    }

    private void renderArc(PoseStack.Pose pose, VertexConsumer ivertexbuilder) {
        Matrix4f lvt_20_1_ = pose.pose();
        Matrix3f lvt_21_1_ = pose.normal();
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0, 0, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0, 1, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1, 1, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1, 0, 1, 0, 1, 240);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!(AlexsMobsClientKeys.getEntity(state) instanceof EntityVoidPortal entityIn)) {
            super.submit(state, matrixStackIn, collector, cameraState);
            return;
        }
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(entityIn.getAttachmentFacing().getOpposite().getRotation());
        matrixStackIn.translate(0.5D, 0, 0.5D);
        matrixStackIn.scale(2F, 2F, 2F);
        renderPortal(entityIn, matrixStackIn, collector, false);
        if (entityIn.isShattered()) {
            float off = 0.01F;
            matrixStackIn.pushPose();
            matrixStackIn.translate(0F, off, 0F);
            renderPortal(entityIn, matrixStackIn, collector, true);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0F, -off, 0F);
            renderPortal(entityIn, matrixStackIn, collector, true);
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
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

    public Identifier getIdleTexture(int age, boolean shattered) {
        if (age < 3) {
            return shattered ? TEXTURE_SHATTERED_0 : TEXTURE_0;
        } else if (age < 6) {
            return shattered ? TEXTURE_SHATTERED_1 : TEXTURE_1;
        } else if (age < 10) {
            return shattered ? TEXTURE_SHATTERED_2 : TEXTURE_2;
        } else {
            return shattered ? TEXTURE_SHATTERED_0 : TEXTURE_0;
        }
    }

    public Identifier getGrowingTexture(int age, boolean shattered) {
        return shattered ? TEXTURE_SHATTERED_PROGRESS[Mth.clamp(age, 0, 9)] : TEXTURE_PROGRESS[Mth.clamp(age, 0, 9)];
    }
}
