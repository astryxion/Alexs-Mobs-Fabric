package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelEmu;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderEmu extends MobRenderer<EntityEmu, LivingEntityRenderState, CitadelEntityModelBridge<EntityEmu>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/emu.png");
    private static final Identifier TEXTURE_BABY = Identifier.parse("alexsmobs:textures/entity/emu_baby.png");
    private static final Identifier TEXTURE_BLONDE = Identifier.parse("alexsmobs:textures/entity/emu_blonde.png");
    private static final Identifier TEXTURE_BLONDE_BABY = Identifier.parse("alexsmobs:textures/entity/emu_baby_blonde.png");
    private static final Identifier TEXTURE_BLUE = Identifier.parse("alexsmobs:textures/entity/emu_blue.png");

    public RenderEmu(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelEmu()), 0.45F);
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
        EntityEmu entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityEmu e ? e : null;
        if (entity == null) {
            return TEXTURE;
        }
        if (entity.getVariant() == 2) {
            return entity.isBaby() ? TEXTURE_BLONDE_BABY : TEXTURE_BLONDE;
        }
        if (entity.getVariant() == 1 && !entity.isBaby()) {
            return TEXTURE_BLUE;
        }
        return entity.isBaby() ? TEXTURE_BABY : TEXTURE;
    }
}
