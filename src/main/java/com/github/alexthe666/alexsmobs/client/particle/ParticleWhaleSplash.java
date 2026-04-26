package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
public class ParticleWhaleSplash  extends WaterDropParticle {

    private ParticleWhaleSplash(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites, RandomSource random) {
        super(level, x, y, z, sprites.get(random));
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.gravity = 0.04F;
        this.yd = 1D;
        this.lifetime = (int)(16.0D / (this.random.nextDouble() * 0.4D + 0.1D));
        this.quadSize = 0.2F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

    }

    public void tick() {
        super.tick();
        if(this.yd < 0D){
            if(Math.abs(this.xd) < 0.23){
                this.xd *= 1.2D;
            }
            if(Math.abs(this.zd) < 0.23){
                this.zd *= 1.2D;
            }
        }
    }
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet p_i50679_1_) {
            this.spriteSet = p_i50679_1_;
        }

        @Override
        public Particle createParticle(SimpleParticleType p_199234_1_, ClientLevel p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_, RandomSource random) {
            ParticleWhaleSplash lvt_15_1_ = new ParticleWhaleSplash(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.spriteSet, random);
            return lvt_15_1_;
        }
    }

}
