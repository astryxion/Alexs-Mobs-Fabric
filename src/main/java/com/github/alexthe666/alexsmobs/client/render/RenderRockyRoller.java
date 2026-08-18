package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelRockyRoller;
import com.github.alexthe666.alexsmobs.entity.EntityRockyRoller;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderRockyRoller extends MobRenderer<EntityRockyRoller, LivingEntityRenderState, CitadelEntityModelBridge<EntityRockyRoller>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/rocky_roller.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/rocky_roller_angry.png");
    private static final Identifier TEXTURE_ROLLING = Identifier.parse("alexsmobs:textures/entity/rocky_roller_rolling.png");

    public RenderRockyRoller(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelRockyRoller()), 0.7F);
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
        EntityRockyRoller entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityRockyRoller r ? r : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isRolling() ? TEXTURE_ROLLING : entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
