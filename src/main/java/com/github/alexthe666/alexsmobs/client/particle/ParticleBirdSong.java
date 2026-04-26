package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ParticleBirdSong extends SingleQuadParticle {

    private ParticleBirdSong(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites, RandomSource random) {
        super(world, x, y, z, xd, yd, zd, sprites.get(random));
        this.friction = 0.7F;
        this.gravity = 0.0F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = 0.15F + this.random.nextFloat() * 0.2F;
        this.lifetime = 20 + this.random.nextInt(20);
        this.setSpriteFromAge(sprites);
        rCol = 0.294F;
        gCol = 0.584F;
        bCol = 1.0F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd = Math.sin(age * 0.3F) * 0.3F;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.yd -= (double) this.gravity;
        }
        float subAlpha = 1F;
        if (this.age > 5) {
            subAlpha = 1 - (float) (this.age - 5) / (this.getLifetime() - 5);
        }
        this.xd *= 0.99D;
        this.yd *= 0.99D;
        this.zd *= 0.99D;
        this.alpha = subAlpha;
        this.roll = (float) (Math.toRadians(Math.sin(age * 0.01F) * 5));

    }

    @Override
    protected int getLightCoords(float partialTick) {
        int packed = super.getLightCoords(partialTick);
        int sky = packed >> 16 & 255;
        return 240 | sky << 16;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ParticleBirdSong(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, random);
        }
    }
}
