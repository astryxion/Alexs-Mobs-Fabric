package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
public class ParticleSkulkBoom extends SingleQuadParticle {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/particle/skulk_boom.png");
    private float size;
    private float prevSize;
    private float prevAlpha;
    private final float alphaDecrease;

    private ParticleSkulkBoom(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ, Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.PARTICLES).getSprite(Identifier.fromNamespaceAndPath("minecraft", "generic_0")));
        this.setSize(1, 0.1F);
        this.setAlpha(1F);
        this.gravity = 0.0F;
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.lifetime = 20 + this.random.nextInt(20);
        this.alphaDecrease = 1F / (float) Math.max(this.lifetime, 1);
        this.size = 0.3F;
        this.prevSize = this.size;
        this.prevAlpha = 1F;
        this.quadSize = this.size;
        this.setColor(1.0F, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevSize = this.size;
        this.prevAlpha = this.alpha;
        this.size += 0.3F;
        this.xd *= 0.1D;
        this.yd *= 0.8D;
        this.zd *= 0.1D;
        if (this.alpha > 0.0F) {
            this.setAlpha(Math.max(this.alpha - this.alphaDecrease, 0.0F));
        }
        this.setSize(1 + this.size, 0.1F);
    }

    @Override
    public void extract(net.minecraft.client.renderer.state.level.QuadParticleRenderState state, net.minecraft.client.Camera camera, float partialTick) {
        float endAlpha = this.alpha;
        float endSize = this.size;
        float saveA = this.alpha;
        float saveQ = this.quadSize;
        this.alpha = Mth.lerp(partialTick, this.prevAlpha, endAlpha);
        this.quadSize = Mth.lerp(partialTick, this.prevSize, endSize);
        super.extract(state, camera, partialTick);
        this.alpha = saveA;
        this.quadSize = saveQ;
    }

    @Override
    protected float getU0() {
        return 0.0F;
    }

    @Override
    protected float getU1() {
        return 1.0F;
    }

    @Override
    protected float getV0() {
        return 0.0F;
    }

    @Override
    protected float getV1() {
        return 1.0F;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return new SingleQuadParticle.Layer(true, TEXTURE, AMRenderTypes.getSkulkBoom().pipeline());
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ParticleSkulkBoom(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
