package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGorilla;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGorillaItem;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderGorilla extends MobRenderer<EntityGorilla, LivingEntityRenderState, CitadelEntityModelBridge<EntityGorilla>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/gorilla.png");
    private static final Identifier TEXTURE_SILVERBACK = Identifier.parse("alexsmobs:textures/entity/gorilla_silverback.png");
    private static final Identifier TEXTURE_DK = Identifier.parse("alexsmobs:textures/entity/gorilla_dk.png");
    private static final Identifier TEXTURE_FUNKY = Identifier.parse("alexsmobs:textures/entity/gorilla_funky.png");

    public RenderGorilla(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelGorilla()), 0.7F);
        this.addLayer(new LayerGorillaItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityGorilla entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGorilla g ? g : null;
        if (entity != null) {
            float s = entity.getGorillaScale();
            matrixStackIn.scale(s, s, s);
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityGorilla entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGorilla g ? g : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isFunkyKong() ? TEXTURE_FUNKY : entity.isDonkeyKong() ? TEXTURE_DK : entity.isSilverback() ? TEXTURE_SILVERBACK : TEXTURE;
    }
}
