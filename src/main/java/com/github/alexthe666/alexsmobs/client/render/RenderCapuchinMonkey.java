package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCapuchinItem;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderCapuchinMonkey extends MobRenderer<EntityCapuchinMonkey, LivingEntityRenderState, CitadelEntityModelBridge<EntityCapuchinMonkey>> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/capuchin_monkey_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/capuchin_monkey_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/capuchin_monkey_2.png");
    private static final Identifier TEXTURE_3 = Identifier.parse("alexsmobs:textures/entity/capuchin_monkey_3.png");

    public RenderCapuchinMonkey(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCapuchinMonkey()), 0.25F);
        this.addLayer(new LayerCapuchinItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void extractRenderState(EntityCapuchinMonkey entity, LivingEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        this.getModel().setCitadelYoung(entity.isBaby());
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityCapuchinMonkey entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityCapuchinMonkey e ? e : null;
        if (entity == null) {
            return TEXTURE_0;
        }
        return switch (entity.getVariant()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            default -> TEXTURE_0;
        };
    }
}
