package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
public class ParticleSimpleHeart extends SingleQuadParticle {

    protected ParticleSimpleHeart(ClientLevel world, double x, double y, double z, SpriteSet sprites, RandomSource random) {
        super(world, x, y, z, sprites.get(random));
        this.xd *= (double) 0.01F;
        this.yd *= (double) 0.01F;
        this.zd *= (double) 0.01F;
        this.yd += 0.1D;
        this.quadSize *= 2F;
        this.lifetime = 32;
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) / (float) this.lifetime * 16.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            if (this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }

            this.xd *= (double) 0.86F;
            this.yd *= (double) 0.86F;
            this.zd *= (double) 0.86F;
            if (this.onGround) {
                this.xd *= (double) 0.7F;
                this.zd *= (double) 0.7F;
            }

        }
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
            return new ParticleSimpleHeart(worldIn, x, y, z, this.spriteSet, random);
        }
    }
}
