package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelHummingbird;
import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderHummingbird extends MobRenderer<EntityHummingbird, LivingEntityRenderState, CitadelEntityModelBridge<EntityHummingbird>> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/hummingbird_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/hummingbird_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/hummingbird_2.png");

    public RenderHummingbird(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelHummingbird()), 0.15F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityHummingbird entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityHummingbird h ? h : null;
        if (entity == null) {
            return TEXTURE_0;
        }
        return entity.getVariant() == 0 ? TEXTURE_0 : entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_2;
    }
}
