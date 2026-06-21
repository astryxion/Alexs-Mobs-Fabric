package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelManedWolf;
import com.github.alexthe666.alexsmobs.entity.EntityManedWolf;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderManedWolf extends MobRenderer<EntityManedWolf, LivingEntityRenderState, CitadelEntityModelBridge<EntityManedWolf>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/maned_wolf.png");
    private static final Identifier TEXTURE_ENDER = Identifier.parse("alexsmobs:textures/entity/maned_wolf_ender.png");

    public RenderManedWolf(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelManedWolf()), 0.45F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityManedWolf entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityManedWolf w ? w : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isEnder() ? TEXTURE_ENDER : TEXTURE;
    }
}
