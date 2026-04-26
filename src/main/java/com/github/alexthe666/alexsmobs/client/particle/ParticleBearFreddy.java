package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
public class ParticleBearFreddy extends Particle {
    public static final ParticleRenderType BEAR_FREDDY = new ParticleRenderType("alexsmobs_bear_freddy");
    private final ModelGrizzlyBear model = new ModelGrizzlyBear();

    ParticleBearFreddy(ClientLevel lvl, double x, double y, double z) {
        super(lvl, x, y, z);
        this.setSize(2, 2);
        this.gravity = 0.0F;
        this.lifetime = 15;
    }

    @Override
    public ParticleRenderType getGroup() {
        return BEAR_FREDDY;
    }

    void submitBear(SubmitNodeCollector collector, float partialTick) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float f = ((float) this.age + partialTick) / (float) this.lifetime;
        float initalFlip = Math.min(f, 0.1F) / 0.1F;
        float laterFlip = Mth.clamp(f - 0.1F, 0F, 0.1F) / 0.1F;
        float scale = 1;
        PoseStack posestack = new PoseStack();
        posestack.mulPose(camera.rotation());
        posestack.translate(0.0D, -1, 0);
        posestack.mulPose(Axis.XP.rotationDegrees(10F - laterFlip * 35F));
        posestack.scale(-scale, -scale, scale);
        posestack.translate(0.0D, 0.5F, 2 + (1F - initalFlip));
        posestack.mulPose(Axis.XP.rotationDegrees(initalFlip * 20F - 5F));
        float swing = laterFlip * (float) Math.sin((this.age + partialTick) * 0.3F) * 20;
        posestack.mulPose(Axis.ZP.rotationDegrees((1F - initalFlip) * 45F + swing));
        boolean baby = this.model.young;
        this.model.young = false;
        this.model.positionForParticle(partialTick, this.age + partialTick);
        collector.submitCustomGeometry(
                posestack,
                AMRenderTypes.getFreddy(RenderGrizzlyBear.TEXTURE_FREDDY),
                (pose, vertexConsumer) -> this.model.renderToBuffer(posestack, vertexConsumer, 240, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
        this.model.young = baby;
    }

    public static final class BearFreddyParticleGroup extends ParticleGroup<ParticleBearFreddy> {
        public BearFreddyParticleGroup(ParticleEngine engine) {
            super(engine);
        }

        @Override
        public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick) {
            List<ParticleBearFreddy> list = new ArrayList<>();
            for (ParticleBearFreddy p : this.getAll()) {
                if (frustum.pointInFrustum(p.x, p.y, p.z)) {
                    list.add(p);
                }
            }
            return new BearFreddyState(list, partialTick);
        }
    }

    private record BearFreddyState(List<ParticleBearFreddy> particles, float partialTick) implements ParticleGroupRenderState {
        @Override
        public void submit(SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
            for (ParticleBearFreddy p : this.particles) {
                p.submitBear(collector, this.partialTick);
            }
        }
    }
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ParticleBearFreddy(worldIn, x, y, z);
        }
    }
}
