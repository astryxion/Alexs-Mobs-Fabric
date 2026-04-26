package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerTigerEyes;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderTiger extends MobRenderer<EntityTiger, LivingEntityRenderState, CitadelEntityModelBridge<EntityTiger>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/tiger/tiger.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_angry.png");
    private static final Identifier TEXTURE_SLEEPING = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_sleeping.png");
    private static final Identifier TEXTURE_WHITE = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_white.png");
    private static final Identifier TEXTURE_ANGRY_WHITE = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_white_angry.png");
    private static final Identifier TEXTURE_SLEEPING_WHITE = Identifier.parse("alexsmobs:textures/entity/tiger/tiger_white_sleeping.png");

    public RenderTiger(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelTiger()), 0.6F);
        this.addLayer(new LayerTigerEyes(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected float getShadowRadius(LivingEntityRenderState state) {
        EntityTiger tiger = AlexsMobsClientKeys.getLiving(state) instanceof EntityTiger t ? t : null;
        if (tiger == null) {
            return super.getShadowRadius(state);
        }
        boolean bodyVisible = this.isBodyVisible(state);
        boolean forceTransparent = !bodyVisible && !state.isInvisibleToPlayer;
        RenderType rt = this.getRenderType(state, bodyVisible, forceTransparent, state.appearsGlowing());
        if (rt == null) {
            return super.getShadowRadius(state);
        }
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float stealthLevel = tiger.prevStealthProgress + (tiger.stealthProgress - tiger.prevStealthProgress) * partialTick;
        return 0.6F * (1 - stealthLevel * 0.1F);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LivingEntityRenderState state, boolean normal, boolean invis, boolean outline) {
        EntityTiger tiger = AlexsMobsClientKeys.getLiving(state) instanceof EntityTiger t ? t : null;
        if (tiger != null && tiger.isStealth()) {
            Identifier texture = this.getTextureLocation(state);
            return RenderTypes.entityTranslucent(texture);
        }
        return super.getRenderType(state, normal, invis, outline);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityTiger entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityTiger t ? t : null;
        if (entity == null) {
            return TEXTURE;
        }
        if (entity.isSleeping()) {
            return entity.isWhite() ? TEXTURE_SLEEPING_WHITE : TEXTURE_SLEEPING;
        } else if (entity.getRemainingPersistentAngerTime() > 0) {
            return entity.isWhite() ? TEXTURE_ANGRY_WHITE : TEXTURE_ANGRY;
        } else {
            return entity.isWhite() ? TEXTURE_WHITE : TEXTURE;
        }
    }
}
