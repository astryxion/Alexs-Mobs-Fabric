package com.github.alexthe666.citadel.client.shader;

import com.github.alexthe666.citadel.ClientProxy;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class CitadelShaderRenderTypes {
    private CitadelShaderRenderTypes() {
    }

    public static final RenderPipeline RAINBOW_AURA_PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(Identifier.parse("citadel:pipeline/rainbow_aura"))
            .withFragmentShader(Identifier.parse("citadel:core/rendertype_rainbow_aura"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withSampler("Sampler1")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();

    private static final Function<Identifier, RenderType> RAINBOW_AURA = Util.memoize(CitadelShaderRenderTypes::buildRainbowAura);

    public static RenderType getRainbowAura(Identifier textureId) {
        return RAINBOW_AURA.apply(textureId);
    }

    private static RenderType buildRainbowAura(Identifier textureId) {
        RenderTarget effectTarget = PostEffectRegistry.getRenderTargetFor(ClientProxy.RAINBOW_AURA_POST_SHADER);
        OutputTarget outputTarget = effectTarget != null
                ? new OutputTarget("citadel_rainbow_aura", () -> effectTarget)
                : OutputTarget.MAIN_TARGET;
        RenderSetup setup = RenderSetup.builder(RAINBOW_AURA_PIPELINE)
                .withTexture("Sampler0", textureId)
                .useLightmap()
                .useOverlay()
                .setOutputTarget(outputTarget)
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .sortOnUpload()
                .createRenderSetup();
        return RenderType.create("citadel_rainbow_aura", setup);
    }
}
