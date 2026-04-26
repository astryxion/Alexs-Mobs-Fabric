package com.github.alexthe666.alexsmobs.client.render;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelCosmicCod;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmicCod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderCosmicCod extends MobRenderer<EntityCosmicCod, LivingEntityRenderState, CitadelEntityModelBridge<EntityCosmicCod>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/cosmic_cod.png");
    private static final Identifier TEXTURE_EYES = Identifier.parse("alexsmobs:textures/entity/cosmic_cod_eyes.png");

    public RenderCosmicCod(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCosmicCod()),  0.25F);
        this.addLayer(new LayerBasicGlow<>(this, TEXTURE_EYES));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}

