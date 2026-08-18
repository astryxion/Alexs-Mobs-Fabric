package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderAnaconda extends MobRenderer<EntityAnaconda, LivingEntityRenderState, CitadelEntityModelBridge<EntityAnaconda>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/anaconda.png");
    private static final Identifier TEXTURE_SHEDDING = Identifier.parse("alexsmobs:textures/entity/anaconda_shedding.png");
    private static final Identifier TEXTURE_YELLOW = Identifier.parse("alexsmobs:textures/entity/anaconda_yellow.png");
    private static final Identifier TEXTURE_YELLOW_SHEDDING = Identifier.parse("alexsmobs:textures/entity/anaconda_yellow_shedding.png");

    public RenderAnaconda(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelAnaconda(AnacondaPartIndex.HEAD)), 0.3F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityAnaconda entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityAnaconda a ? a : null;
        if (entitylivingbaseIn != null) {
            float sc = entitylivingbaseIn.getAnacondaMobScale();
            matrixStackIn.scale(sc, sc, sc);
        }
    }

    public static Identifier getAnacondaTexture(boolean yellow, boolean shedding) {
        return yellow ? shedding ? TEXTURE_YELLOW_SHEDDING : TEXTURE_YELLOW : shedding ? TEXTURE_SHEDDING : TEXTURE;
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityAnaconda entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityAnaconda a ? a : null;
        if (entity == null) {
            return getAnacondaTexture(false, false);
        }
        return getAnacondaTexture(entity.isYellow(), entity.isShedding());
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
}
