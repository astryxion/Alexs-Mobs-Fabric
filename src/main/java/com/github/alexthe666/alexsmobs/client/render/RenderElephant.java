package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerElephantItem;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerElephantOverlays;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderElephant extends MobRenderer<EntityElephant, LivingEntityRenderState, CitadelEntityModelBridge<EntityElephant>> {
    private static final Identifier TEXTURE_TUSK = Identifier.parse("alexsmobs:textures/entity/elephant/elephant_tusks.png");
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/elephant/elephant.png");

    public RenderElephant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelElephant(0)), 1.4F);
        this.addLayer(new LayerElephantOverlays(this));
        this.addLayer(new LayerElephantItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityElephant entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityElephant e ? e : null;
        if (entitylivingbaseIn != null && entitylivingbaseIn.isTusked()) {
            matrixStackIn.scale(1.1F, 1.1F, 1.1F);
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityElephant entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityElephant e ? e : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isTusked() && !entity.isBaby() ? TEXTURE_TUSK : TEXTURE;
    }
}
