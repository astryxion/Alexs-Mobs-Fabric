package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
public class ParticleHemolymph extends SingleQuadParticle {

    private static final int[] POSSIBLE_COLORS = {0X70FFF8, 0X3BFFD0, 0X08DED9};

    private ParticleHemolymph(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites, RandomSource random) {
        super(world, x, y, z, motionX, motionY, motionZ, sprites.get(random));
        int color = POSSIBLE_COLORS[random.nextInt(POSSIBLE_COLORS.length - 1)];
        float lvt_18_1_ = (float) (color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float) (color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float) (color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        this.xd = (float) motionX;
        this.yd = (float) motionY;
        this.zd = (float) motionZ;
        this.quadSize *= 0.6F + this.random.nextFloat() * 1.4F;
        this.lifetime = 10 + this.random.nextInt(15);
        this.gravity = 0.5F;

    }

    @Override
    public void tick() {
        super.tick();
        this.yd -= 0.004D + 0.04D * (double) this.gravity;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.OPAQUE;
    }
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ParticleHemolymph(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, random);
        }
    }
}
