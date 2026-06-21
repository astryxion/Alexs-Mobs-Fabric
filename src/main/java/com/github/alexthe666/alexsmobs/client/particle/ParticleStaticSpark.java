package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
public class ParticleStaticSpark extends SingleQuadParticle {
    private int decrement = 1;
    private int textureIndex = 0;
    private float sparkSize;

    private static TextureAtlasSprite getSprite(ClientLevel level, int index) {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.PARTICLES);
        return atlas.getSprite(Identifier.fromNamespaceAndPath("minecraft", "generic_" + index));
    }

    private ParticleStaticSpark(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int texIndex) {
        super(world, x, y, z, motionX, motionY, motionZ, getSprite(world, texIndex));
        this.setSize(1, 1);
        this.gravity = 0.0F;
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.lifetime = 10 + this.random.nextInt(15);
        this.textureIndex = Mth.clamp(texIndex, 0, 7);
        this.decrement = this.textureIndex > 0 ? this.lifetime / this.textureIndex : this.lifetime;
        this.sparkSize = this.random.nextFloat() * 0.2F + 0.2F;
        this.quadSize = this.sparkSize;
        this.setColor(1.0F, 1.0F, 1.0F);
        this.setAlpha(1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.xd *= 0.97D;
        this.yd *= 0.97D;
        this.zd *= 0.97D;
        if (this.textureIndex > 0) {
            if (this.age % this.decrement == 0) {
                this.textureIndex--;
                this.setSprite(getSprite(this.level, this.textureIndex));
            }
        }
        if (this.sparkSize > 0.2F) {
            this.sparkSize -= 0.015F;
        }
        this.quadSize = this.sparkSize;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return 240;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            int idx = Mth.clamp(random.nextInt(8), 0, 7);
            return new ParticleStaticSpark(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, idx);
        }
    }
}
