package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
public class ParticleGusterSandShot extends SingleQuadParticle {

    private ParticleGusterSandShot(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int variant, SpriteSet sprites, RandomSource random) {
        super(world, x, y, z, motionX, motionY, motionZ, sprites.get(random));
        int color = ParticleGusterSandSpin.selectColor(variant, random);
        float lvt_18_1_ = (float) (color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float) (color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float) (color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        this.xd = (float) motionX;
        this.yd = (float) motionY;
        this.zd = (float) motionZ;
        this.quadSize *= 0.6F + random.nextFloat() * 1.4F;
        this.lifetime = 10 + random.nextInt(15);
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
            return new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0, spriteSet, random);
        }
    }
    public static class FactoryRed implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FactoryRed(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1, spriteSet, random);
        }
    }
    public static class FactorySoul implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FactorySoul(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 2, spriteSet, random);
        }
    }
}
