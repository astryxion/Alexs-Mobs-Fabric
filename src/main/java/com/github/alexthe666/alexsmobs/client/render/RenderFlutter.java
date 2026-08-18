package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelFlutter;
import com.github.alexthe666.alexsmobs.client.model.ModelFlutterPotted;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderFlutter extends MobRenderer<EntityFlutter, LivingEntityRenderState, CitadelEntityModelBridge<EntityFlutter>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/flutter.png");
    private static final Identifier TEXTURE_EYES = Identifier.parse("alexsmobs:textures/entity/flutter_eyes.png");
    private final CitadelEntityModelBridge<EntityFlutter> flutterBridge;
    private final CitadelEntityModelBridge<EntityFlutter> pottedBridge;

    @SuppressWarnings("unchecked")
    public RenderFlutter(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelFlutter()), 0.25F);
        this.flutterBridge = (CitadelEntityModelBridge<EntityFlutter>) (Object) this.model;
        this.pottedBridge = new CitadelEntityModelBridge<>(new ModelFlutterPotted());
        this.addLayer(new LayerBasicGlow<>(this, TEXTURE_EYES));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityFlutter entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityFlutter f ? f : null;
        if (entity != null) {
            this.model = entity.isPotted() ? this.pottedBridge : this.flutterBridge;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
