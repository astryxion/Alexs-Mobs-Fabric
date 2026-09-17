package com.github.alexthe666.citadel.client.shader;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class CitadelShaderRenderTypes {
    private CitadelShaderRenderTypes() {
    }

    public static final RenderPipeline RAINBOW_AURA_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(Identifier.parse("citadel:pipeline/rainbow_aura"))
            .withFragmentShader(Identifier.parse("citadel:core/rendertype_rainbow_aura"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build());


    private static final Function<Identifier, RenderType> RAINBOW_AURA = Util.memoize(CitadelShaderRenderTypes::buildRainbowAura);

    public static RenderType getRainbowAura(Identifier textureId) {
        return RAINBOW_AURA.apply(textureId);
    }

    private static RenderType buildRainbowAura(Identifier textureId) {
        RenderSetup setup = RenderSetup.builder(RAINBOW_AURA_PIPELINE)
                .withTexture("Sampler0", textureId)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .sortOnUpload()
                .createRenderSetup();
        return RenderType.create("citadel_rainbow_aura", setup);
    }
}
