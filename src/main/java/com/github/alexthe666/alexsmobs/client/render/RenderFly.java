package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelFly;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderFly extends MobRenderer<EntityFly, LivingEntityRenderState, CitadelEntityModelBridge<EntityFly>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/fly.png");

    public RenderFly(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelFly()), 0.2F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(LivingEntityRenderState state, PoseStack matrixStackIn, float ageInTicks, float rotationYaw) {
        EntityFly fly = AlexsMobsClientKeys.getLiving(state) instanceof EntityFly ef ? ef : null;
        if (fly != null && this.isShaking(fly)) {
            rotationYaw += (float) (Math.cos((double) fly.tickCount * 7F) * Math.PI * (double) 0.9F);
            float vibrate = 0.05F;
            matrixStackIn.translate((fly.getRandom().nextFloat() - 0.5F) * vibrate, (fly.getRandom().nextFloat() - 0.5F) * vibrate, (fly.getRandom().nextFloat() - 0.5F) * vibrate);
        }
        super.setupRotations(state, matrixStackIn, ageInTicks, rotationYaw);
    }

    protected boolean isShaking(EntityFly fly) {
        return fly.isInNether();
    }
}
