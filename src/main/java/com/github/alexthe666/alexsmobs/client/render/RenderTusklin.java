package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelTusklin;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerTusklinGear;
import com.github.alexthe666.alexsmobs.entity.EntityTusklin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderTusklin extends MobRenderer<EntityTusklin, LivingEntityRenderState, CitadelEntityModelBridge<EntityTusklin>> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/tusklin.png");

    public RenderTusklin(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelTusklin()), 1.0F);
        this.addLayer(new LayerTusklinGear(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected boolean isShaking(LivingEntityRenderState state) {
        EntityTusklin entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityTusklin t ? t : null;
        return entity != null && entity.isInNether();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
