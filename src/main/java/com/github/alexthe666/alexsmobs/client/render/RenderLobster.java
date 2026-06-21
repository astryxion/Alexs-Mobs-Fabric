package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelLobster;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderLobster extends MobRenderer<EntityLobster, LivingEntityRenderState, CitadelEntityModelBridge<EntityLobster>> {
    private static final Identifier TEXTURE_RED = Identifier.parse("alexsmobs:textures/entity/lobster_red.png");
    private static final Identifier TEXTURE_BLUE = Identifier.parse("alexsmobs:textures/entity/lobster_blue.png");
    private static final Identifier TEXTURE_YELLOW = Identifier.parse("alexsmobs:textures/entity/lobster_yellow.png");
    private static final Identifier TEXTURE_REDBLUE = Identifier.parse("alexsmobs:textures/entity/lobster_redblue.png");
    private static final Identifier TEXTURE_BLACK = Identifier.parse("alexsmobs:textures/entity/lobster_black.png");
    private static final Identifier TEXTURE_WHITE = Identifier.parse("alexsmobs:textures/entity/lobster_white.png");

    public RenderLobster(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelLobster()), 0.25F);
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
        EntityLobster entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityLobster l ? l : null;
        if (entity == null) {
            return TEXTURE_RED;
        }
        return switch (entity.getVariant()) {
            case 1 -> TEXTURE_BLUE;
            case 2 -> TEXTURE_YELLOW;
            case 3 -> TEXTURE_REDBLUE;
            case 4 -> TEXTURE_BLACK;
            case 5 -> TEXTURE_WHITE;
            default -> TEXTURE_RED;
        };
    }
}
