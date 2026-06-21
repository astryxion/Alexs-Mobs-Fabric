package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelMudskipper;
import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderMudskipper extends MobRenderer<EntityMudskipper, LivingEntityRenderState, CitadelEntityModelBridge<EntityMudskipper>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/mudskipper.png");
    private static final Identifier TEXTURE_SPIT = Identifier.parse("alexsmobs:textures/entity/mudskipper_spit.png");

    public RenderMudskipper(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelMudskipper()), 0.25F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityMudskipper entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityMudskipper m ? m : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isMouthOpen() ? TEXTURE_SPIT : TEXTURE;
    }
}
