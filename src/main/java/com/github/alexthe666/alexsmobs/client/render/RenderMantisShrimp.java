package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelMantisShrimp;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerMantisShrimpItem;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderMantisShrimp extends MobRenderer<EntityMantisShrimp, LivingEntityRenderState, CitadelEntityModelBridge<EntityMantisShrimp>> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/mantis_shrimp_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/mantis_shrimp_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/mantis_shrimp_2.png");
    private static final Identifier TEXTURE_3 = Identifier.parse("alexsmobs:textures/entity/mantis_shrimp_3.png");

    public RenderMantisShrimp(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelMantisShrimp()), 0.6F);
        this.addLayer(new LayerMantisShrimpItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityMantisShrimp entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityMantisShrimp m ? m : null;
        if (entity == null) {
            return TEXTURE_0;
        }
        return entity.getVariant() == 3 ? TEXTURE_3 : entity.getVariant() == 2 ? TEXTURE_2 : entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
