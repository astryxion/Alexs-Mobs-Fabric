package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerVoidWormGlow;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class RenderVoidWormHead extends MobRenderer<EntityVoidWorm, LivingEntityRenderState, CitadelEntityModelBridge<EntityVoidWorm>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_head.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_head_glow.png");

    public RenderVoidWormHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelVoidWorm(0.0f)), 1F);
        this.addLayer(new LayerVoidWormGlow<CitadelEntityModelBridge<EntityVoidWorm>>(this, renderManagerIn.getResourceManager()) {
            @Override
            public Identifier getGlowTexture(LivingEntity worm) {
                return TEXTURE_GLOW;
            }

            @Override
            public boolean isGlowing(LivingEntity worm) {
                return true;
            }

            @Override
            public float getAlpha(LivingEntity livingEntity) {
                return 1.0F;
            }
        });
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LivingEntityRenderState state, boolean normal, boolean invis, boolean outline) {
        Identifier id = this.getTextureLocation(state);
        if (invis) {
            return RenderTypes.entityTranslucentCullItemTarget(id);
        } else if (normal) {
            return RenderTypes.entityTranslucent(id);
        } else {
            return outline ? RenderTypes.outline(id) : null;
        }
    }

    @Override
    public boolean shouldRender(EntityVoidWorm worm, Frustum camera, double camX, double camY, double camZ) {
        return worm.getPortalTicks() <= 0 && super.shouldRender(worm, camera, camX, camY, camZ);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
