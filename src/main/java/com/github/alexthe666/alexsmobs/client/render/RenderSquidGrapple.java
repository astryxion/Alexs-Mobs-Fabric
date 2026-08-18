package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelSquidGrapple;
import com.github.alexthe666.alexsmobs.entity.EntitySquidGrapple;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderSquidGrapple extends EntityRenderer<EntitySquidGrapple, EntityRenderState> {

    private static final Identifier SQUID_TEXTURE = Identifier.parse("alexsmobs:textures/entity/giant_squid.png");
    private static final ModelSquidGrapple SQUID_MODEL = new ModelSquidGrapple();
    /** Citadel models read {@link com.mojang.blaze3d.vertex.PoseStack#last()} at emit time; must use queued submit pose. */
    private final PoseStack citadelPoseScratch = new PoseStack();
    private static final float TENTACLES_COLOR_R = 181F / 255F;
    private static final float TENTACLES_COLOR_G = 87F / 255F;
    private static final float TENTACLES_COLOR_B = 85F / 255F;
    private static final float TENTACLES_COLOR_R2 = 191F / 255F;
    private static final float TENTACLES_COLOR_G2 = 98F / 255F;
    private static final float TENTACLES_COLOR_B2 = 89F / 255F;

    public RenderSquidGrapple(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    private static void addVertexPairAlex(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float) p_174321_ / 24.0F;
        int i = (int) Mth.lerp(f, (float) p_174313_, (float) p_174314_);
        int j = (int) Mth.lerp(f, (float) p_174315_, (float) p_174316_);
        int k = LightCoordsUtil.pack(i, j);
        float f2 = TENTACLES_COLOR_R;
        float f3 = TENTACLES_COLOR_G;
        float f4 = TENTACLES_COLOR_B;
        if (p_174321_ % 2 == (p_174322_ ? 1 : 0)) {
            f2 = TENTACLES_COLOR_R2;
            f3 = TENTACLES_COLOR_G2;
            f4 = TENTACLES_COLOR_B2;
        }
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.addVertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).setColor((int) (f2 * 255), (int) (f3 * 255), (int) (f4 * 255), 255).setLight(k);
        p_174308_.addVertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).setColor((int) (f2 * 255), (int) (f3 * 255), (int) (f4 * 255), 255).setLight(k);
    }

    public static <E extends Entity> void renderTentacle(Entity mob, float partialTick, PoseStack p_115464_, SubmitNodeCollector collector, LivingEntity player, boolean left, float zOffset) {
        p_115464_.pushPose();
        float bodyRot = mob instanceof LivingEntity ? ((LivingEntity) mob).yBodyRot : mob.getYRot();
        float bodyRot0 = mob instanceof LivingEntity ? ((LivingEntity) mob).yBodyRotO : mob.yRotO;
        Vec3 vec3 = player.getRopeHoldPosition(partialTick);
        double d0 = (double) (Mth.lerp(partialTick, bodyRot, bodyRot0) * Mth.DEG_TO_RAD) + (Math.PI / 2D);
        Vec3 vec31 = new Vec3(0, 0F, 0);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp(partialTick, mob.xo, mob.getX()) + d1;
        double d4 = Mth.lerp(partialTick, mob.yo, mob.getY()) + vec31.y;
        double d5 = Mth.lerp(partialTick, mob.zo, mob.getZ()) + d2;
        p_115464_.translate(d3, d4, d5);
        float f = (float) (vec3.x - d3);
        float f1 = (float) (vec3.y - d4);
        float f2 = (float) (vec3.z - d5);
        Matrix4f matrix4f = p_115464_.last().pose();
        float f4 = (float) (Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F);
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = AMBlockPos.fromVec3(mob.getEyePosition(partialTick));
        BlockPos blockpos1 = AMBlockPos.fromVec3(player.getEyePosition(partialTick));
        int i = getTentacleLightLevel(mob, blockpos);
        int j = mob.level().getBrightness(LightLayer.BLOCK, blockpos1);
        int k = mob.level().getBrightness(LightLayer.SKY, blockpos);
        int l = mob.level().getBrightness(LightLayer.SKY, blockpos1);
        float width = 0.2F;
        collector.submitCustomGeometry(p_115464_, RenderTypes.leash(), (pose, vertexconsumer) -> {
            Matrix4f mat = pose.pose();
            for (int i1 = 0; i1 <= 24; ++i1) {
                addVertexPairAlex(vertexconsumer, mat, f, f1, f2, i, j, k, l, width, width, f5, f6, i1, false);
            }
            for (int j1 = 24; j1 >= 0; --j1) {
                addVertexPairAlex(vertexconsumer, mat, f, f1, f2, i, j, k, l, width, width, f5, f6, j1, true);
            }
        });
        p_115464_.popPose();
    }

    protected static int getTentacleLightLevel(Entity p_114496_, BlockPos p_114497_) {
        return p_114496_.isOnFire() ? 15 : p_114496_.level().getBrightness(LightLayer.BLOCK, p_114497_);
    }

    public boolean shouldRender(EntitySquidGrapple grapple, Frustum f, double d1, double d2, double d3) {
        return super.shouldRender(grapple, f, d1, d2, d3) || grapple.getOwner() != null && (f.isVisible(grapple.getOwner().getBoundingBox()) || grapple.getOwner() == Minecraft.getInstance().player);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        Entity raw = AlexsMobsClientKeys.getEntity(state);
        if (!(raw instanceof EntitySquidGrapple entityIn)) {
            super.submit(state, matrixStackIn, collector, cameraState);
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        int packedLightIn = state.lightCoords;

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        matrixStackIn.translate(0, -1.5F, -0.25F);
        collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(SQUID_TEXTURE), (submitPose, ivertexbuilder) ->
            AlexAdvancedEntityModel.withCitadelSubmitPose(submitPose, this.citadelPoseScratch, scratch ->
                SQUID_MODEL.renderToBuffer(scratch, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1))
        );
        matrixStackIn.popPose();

        if (entityIn.getOwner() instanceof LivingEntity holder) {
            double d0 = Mth.lerp(partialTicks, entityIn.xo, entityIn.getX());
            double d1 = Mth.lerp(partialTicks, entityIn.yo, entityIn.getY());
            double d2 = Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ());
            matrixStackIn.pushPose();
            matrixStackIn.translate(-d0, -d1, -d2);
            renderTentacle(entityIn, partialTicks, matrixStackIn, collector, holder, holder.getMainArm() != HumanoidArm.LEFT, -0.1F);
            matrixStackIn.popPose();
        }

        super.submit(state, matrixStackIn, collector, cameraState);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return SQUID_TEXTURE;
    }

    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, VertexConsumer p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.addVertex(p_229039_1_, (float) p_229039_4_, (float) p_229039_5_, (float) p_229039_6_)
            .setColor(255, 255, 255, 255)
            .setUv(p_229039_7_, p_229039_8_)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(p_229039_12_)
            .setNormal((float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_);
    }
}
