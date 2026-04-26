package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelEndergrade;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerEndergradeSaddle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderEndergrade extends MobRenderer<EntityEndergrade, LivingEntityRenderState, CitadelEntityModelBridge<EntityEndergrade>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/endergrade.png");

    public RenderEndergrade(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelEndergrade()), 0.6F);
        this.addLayer(new LayerEndergradeSaddle(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LivingEntityRenderState state, boolean normal, boolean invis, boolean outline) {
        Identifier id = this.getTextureLocation(state);
        if (invis) {
            return RenderTypes.entityTranslucentCullItemTarget(id);
        } else if (normal) {
            return RenderTypes.entityTranslucent(id);
        } else {
            return outline ? RenderTypes.outline(id) : null;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
