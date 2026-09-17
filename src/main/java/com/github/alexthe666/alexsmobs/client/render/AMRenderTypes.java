package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

/**
 * Custom {@link RenderType} factories for Alex's Mobs. Minecraft 26.1 uses {@link RenderSetup} instead of
 * {@code RenderStateShard} / {@code CompositeState}.
 */
public final class AMRenderTypes {

    private AMRenderTypes() {
    }

    public static final Identifier STATIC_TEXTURE = Identifier.parse("alexsmobs:textures/static.png");

    private static boolean encounteredMultiConsumerError = false;

    private static Matrix4f rainbowMatrix1(float in, long time) {
        long i = Util.getMillis() * time;
        float f1 = (float) (i % 30000L) / 30000.0F;
        Matrix4f matrix4f = new Matrix4f().translation(0, f1, 0.0F);
        matrix4f.scale(in);
        return matrix4f;
    }

    private static Matrix4f rainbowMatrix2(float in, long time) {
        long i = Util.getMillis() * time;
        float f1 = (float) (i % 30000L) / 30000.0F;
        float f2 = (float) Math.sin(i / 30000F);
        Matrix4f matrix4f = new Matrix4f().translation(f1, f2, 0.0F);
        matrix4f.scale(in);
        return matrix4f;
    }

    private static Matrix4f staticMatrix(float in, long time) {
        long i = Util.getMillis() * time;
        float f1 = (float) (i % 30000L) / 30000.0F;
        float f2 = (float) Math.floor((i % 3000L) / 3000.0F * 4.0F);
        float f3 = (float) Math.sin(i / 30000F) * 0.05F;
        Matrix4f matrix4f = new Matrix4f().translation(f1, f2 * 0.25F + f3, 0.0F);
        matrix4f.scale(in * 1.5F, in * 0.25F, in);
        return matrix4f;
    }

    private static final TextureTransform RAINBOW_TEXTURING = new TextureTransform("entity_glint_texturing", () -> rainbowMatrix1(1.2F, 4L));
    private static final TextureTransform COMB_JELLY_TEXTURING = new TextureTransform("entity_glint_texturing", () -> rainbowMatrix1(2F, 16L));
    private static final TextureTransform RAINBOW_TEXTURING_LARGE = new TextureTransform("entity_glint_texturing", () -> rainbowMatrix2(5F, 14L));
    private static final TextureTransform WEEZER_TEXTURING = new TextureTransform("entity_glint_texturing", () -> rainbowMatrix2(7F, 16L));
    private static final TextureTransform STATIC_PORTAL_TEXTURING = new TextureTransform("entity_glint_texturing", () -> staticMatrix(1.1F, 12L));
    private static final TextureTransform STATIC_PARTICLE_TEXTURING = new TextureTransform("entity_glint_texturing", () -> staticMatrix(0.1F, 12L));
    private static final TextureTransform STATIC_ENTITY_TEXTURING = new TextureTransform("entity_glint_texturing", () -> staticMatrix(3F, 12L));

    /**
     * Entity overlay with a scrolling texture matrix. Vanilla {@link RenderPipelines#GLINT} is item-enchantment
     * geometry ({@code POSITION_TEX}, additive glint blend) and turns living models into a solid white silhouette.
     * Vanilla {@link RenderPipelines#ENERGY_SWIRL} is the same shader path but additive, which also blows out skin quads.
     */
    public static final RenderPipeline UNDERMINER_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withLocation(Identifier.parse("alexsmobs:pipeline/underminer"))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build();

    /**
     * Scrolling rainbow overlay on living models. Vanilla {@link RenderPipelines#GLINT} is item geometry
     * ({@code POSITION_TEX}) and cannot draw entity meshes. {@link RenderPipelines#BREEZE_WIND} is translucent
     * and replaces the skin with an opaque rainbow. This matches original Alex's Mobs: entity shader +
     * {@link BlendFunction#GLINT} + equal depth so the skin stays and the rainbow tints it.
     */
    public static final RenderPipeline RAINBOW_OVERLAY_PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(Identifier.parse("alexsmobs:pipeline/rainbow_overlay"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
            .withCull(false)
            .withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
            .build();

    private static RenderType glintType(String name, Identifier texture, TextureTransform transform) {
        RenderSetup setup = RenderSetup.builder(RAINBOW_OVERLAY_PIPELINE)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .setTextureTransform(transform)
                .sortOnUpload()
                .createRenderSetup();
        return RenderType.create(name, setup);
    }

    public static final RenderType COMBJELLY_RAINBOW_GLINT = glintType(
            "cj_rainbow_glint",
            Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_rainbow.png"),
            COMB_JELLY_TEXTURING);

    public static final RenderType RAINBOW_GLINT = glintType("rainbow_glint", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_rainbow.png"), RAINBOW_TEXTURING);
    public static final RenderType TRANS_GLINT = glintType("trans_glint", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_trans.png"), RAINBOW_TEXTURING);
    public static final RenderType NONBI_GLINT = glintType("nonbi_glint", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_nonbi.png"), RAINBOW_TEXTURING);
    public static final RenderType BI_GLINT = glintType("bi_glint", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_bi.png"), RAINBOW_TEXTURING);
    public static final RenderType ACE_GLINT = glintType("ace_glint", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_ace.png"), RAINBOW_TEXTURING);
    public static final RenderType BRAZIL_GLINT = glintType("brazil_glint", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_brazil.png"), RAINBOW_TEXTURING_LARGE);
    public static final RenderType WEEZER_GLINT = glintType("weezer_glint", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_weezer.png"), WEEZER_TEXTURING);

    public static final RenderType STATIC_PORTAL = glintType("static_portal", STATIC_TEXTURE, STATIC_PORTAL_TEXTURING);
    public static final RenderType STATIC_PARTICLE = glintType("static_particle", STATIC_TEXTURE, STATIC_PARTICLE_TEXTURING);
    public static final RenderType STATIC_ENTITY = glintType("static_entity", STATIC_TEXTURE, STATIC_ENTITY_TEXTURING);

    /** Same layered end portal as vanilla {@link RenderTypes#endPortal()}. */
    public static final RenderType VOID_WORM_PORTAL_OVERLAY = RenderTypes.endPortal();

    private static final RenderPipeline EYES_SHADER = RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE;

    private static final TextureTransform WORM_TRANSPARENCY = new TextureTransform("worm_translucent", () -> new Matrix4f());

    protected static final TextureTransform MIMICUBE_TRANSPARENCY = new TextureTransform("mimicube_transparency", () -> new Matrix4f());

    private static final TextureTransform GHOST_TRANSPARENCY = new TextureTransform("ghost_transparency", () -> new Matrix4f());

    /**
     * 26.1 {@link net.minecraft.client.renderer.RenderPipelines#ENTITY_CUTOUT} already uses {@code withCull(false)};
     * this matches {@link RenderTypes#entityCutout(Identifier)} for texture-backed entity geometry.
     */
    public static RenderType entityCutoutNoCull(Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    public static RenderType entityTranslucent(Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    public static RenderType getTransparentMimicube(Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    public static RenderType getEyesFlickering(Identifier texture, float lightLevel) {
        return RenderTypes.entityTranslucentEmissive(texture);
    }

    public static RenderType getFullBright(Identifier texture) {
        return RenderTypes.entityTranslucentEmissive(texture);
    }

    public static RenderType getFreddy(Identifier texture) {
        RenderSetup setup = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
                .withTexture("Sampler0", texture)
                .useOverlay()
                .createRenderSetup();
        return RenderType.create("freddy", setup);
    }

    public static RenderType getFrilledSharkTeeth(Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    public static RenderType getEyesNoCull(Identifier texture) {
        return RenderTypes.eyes(texture);
    }

    public static RenderType getSpectreBones(Identifier texture) {
        RenderSetup setup = RenderSetup.builder(EYES_SHADER)
                .withTexture("Sampler0", texture)
                .useOverlay()
                .setTextureTransform(GHOST_TRANSPARENCY)
                .createRenderSetup();
        return RenderType.create("spectre_bones", setup);
    }

    public static RenderType getGhost(Identifier texture) {
        RenderSetup setup = RenderSetup.builder(EYES_SHADER)
                .withTexture("Sampler0", texture)
                .useOverlay()
                .setTextureTransform(GHOST_TRANSPARENCY)
                .createRenderSetup();
        return RenderType.create("ghost_am", setup);
    }

    public static RenderType getEyesAlphaEnabled(Identifier locationIn) {
        RenderSetup setup = RenderSetup.builder(EYES_SHADER)
                .withTexture("Sampler0", locationIn)
                .useLightmap()
                .useOverlay()
                .setTextureTransform(WORM_TRANSPARENCY)
                .createRenderSetup();
        return RenderType.create("eye_alpha", setup);
    }

    /** Glowing eyes / beams visible through fog; matches vanilla {@link RenderTypes#eyes(Identifier)}. */
    public static RenderType getEyesNoFog(Identifier locationIn) {
        return RenderTypes.eyes(locationIn);
    }

    public static RenderType getSunbirdShine() {
        return glintType("sunbird_shine", Identifier.parse("alexsmobs:textures/entity/sunbird_shine.png"), TextureTransform.GLINT_TEXTURING);
    }

    public static final Identifier SKULK_BOOM_TEXTURE = Identifier.parse("alexsmobs:textures/particle/skulk_boom.png");

    /**
     * Energy-swirl entity shader with translucent blend (original shockwave), not vanilla additive {@code energySwirl}.
     */
    public static final RenderPipeline SKULK_BOOM_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withLocation(Identifier.parse("alexsmobs:pipeline/skulk_boom"))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build();

    private static final RenderType SKULK_BOOM_TYPE = RenderType.create(
            "skulk_boom",
            RenderSetup.builder(SKULK_BOOM_PIPELINE)
                    .withTexture("Sampler0", SKULK_BOOM_TEXTURE)
                    .setTextureTransform(new TextureTransform.OffsetTextureTransform(0.0F, 0.0F))
                    .useLightmap()
                    .useOverlay()
                    .sortOnUpload()
                    .createRenderSetup()
    );

    public static RenderType getSkulkBoom() {
        return SKULK_BOOM_TYPE;
    }

    private static final Function<Identifier, RenderType> UNDERMINER_TYPE = Util.memoize(
            texture -> {
                RenderSetup state = RenderSetup.builder(UNDERMINER_PIPELINE)
                        .withTexture("Sampler0", texture)
                        .setTextureTransform(new TextureTransform.OffsetTextureTransform(0.0F, 0.0F))
                        .useLightmap()
                        .useOverlay()
                        .sortOnUpload()
                        .createRenderSetup();
                return RenderType.create("underminer", state);
            });

    public static RenderType getUnderminer(Identifier texture) {
        return UNDERMINER_TYPE.apply(texture);
    }

    /**
     * Same visual pipeline as 1.21.1 {@code getGhostPickaxe}: item-entity translucent entity shader, lightning blend,
     * no cull, item-entity output target (see {@link RenderTypes} {@code ENTITY_TRANSLUCENT_CULL_ITEM_TARGET} but with
     * {@link RenderTypes#entityTranslucentCull} ({@link RenderPipelines#ENTITY_TRANSLUCENT_CULL}) but with
     * {@link BlendFunction#LIGHTNING} and no cull. {@link BindGroupLayouts#SAMPLER1} matches vanilla translucent-cull
     * item-entity layout without duplicating {@code Sampler0} from {@link RenderPipelines#ENTITY_SNIPPET}.
     */
    public static final RenderPipeline GHOST_PICKAXE_PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(Identifier.parse("alexsmobs:pipeline/ghost_pickaxe"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .build();

    private static final Function<Identifier, RenderType> GHOST_PICKAXE_TYPE = Util.memoize(
            texture -> {
                RenderSetup state = RenderSetup.builder(GHOST_PICKAXE_PIPELINE)
                        .withTexture("Sampler0", texture)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .sortOnUpload()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderType.create("ghost_pickaxe", state);
            });

    public static RenderType getGhostPickaxe(Identifier texture) {
        return GHOST_PICKAXE_TYPE.apply(texture);
    }

    public static RenderType getGhostCrumbling(Identifier texture) {
        return RenderTypes.crumbling(texture);
    }

    public static RenderType getFarseerBeam() {
        RenderSetup setup = RenderSetup.builder(RenderPipelines.ENERGY_SWIRL)
                .withTexture("Sampler0", STATIC_TEXTURE)
                .useLightmap()
                .useOverlay()
                .setLayeringTransform(net.minecraft.client.renderer.rendertype.LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .createRenderSetup();
        return RenderType.create("farseer_beam", setup);
    }

    public static class BufferSource {
        private final StagedVertexBuffer stagedVertexBuffer;
        private final Map<RenderType, StagedVertexBuffer.Draw> draws = new HashMap<>();

        public BufferSource(StagedVertexBuffer stagedVertexBuffer) {
            this.stagedVertexBuffer = stagedVertexBuffer;
        }

        public VertexConsumer getBuffer(RenderType renderType) {
            StagedVertexBuffer.Draw draw = draws.computeIfAbsent(renderType, rt -> {
                VertexSorting sorting = rt.sortOnUpload() ? VertexSorting.byDistance(0.0F, 0.0F, 0.0F) : null;
                return stagedVertexBuffer.appendDraw(rt.format(), rt.primitiveTopology(), sorting);
            });
            return stagedVertexBuffer.getVertexBuilder(draw);
        }

        public void endBatch(com.mojang.renderpearl.api.commands.RenderPass renderPass) {
            if (draws.isEmpty()) {
                return;
            }
            stagedVertexBuffer.upload();
            for (Map.Entry<RenderType, StagedVertexBuffer.Draw> entry : draws.entrySet()) {
                StagedVertexBuffer.ExecuteInfo info = stagedVertexBuffer.getExecuteInfo(entry.getValue());
                if (info != null) {
                    entry.getKey().prepare().drawFromBuffer(info, renderPass);
                }
            }
            stagedVertexBuffer.endDraw();
            draws.clear();
        }
    }

    private static final class MergedVertexConsumer implements VertexConsumer {
        private final VertexConsumer first;
        private final VertexConsumer second;

        private MergedVertexConsumer(VertexConsumer first, VertexConsumer second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.first.addVertex(x, y, z);
            this.second.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            this.first.setColor(r, g, b, a);
            this.second.setColor(r, g, b, a);
            return this;
        }

        @Override
        public VertexConsumer setColor(int color) {
            this.first.setColor(color);
            this.second.setColor(color);
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.first.setUv(u, v);
            this.second.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            this.first.setUv1(u, v);
            this.second.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            this.first.setUv2(u, v);
            this.second.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv3(float u, float v) {
            this.first.setUv3(u, v);
            this.second.setUv3(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            this.first.setNormal(x, y, z);
            this.second.setNormal(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            this.first.setLineWidth(width);
            this.second.setLineWidth(width);
            return this;
        }
    }

    public static VertexConsumer createMergedVertexConsumer(VertexConsumer consumer1, VertexConsumer consumer2) {
        VertexConsumer vertexConsumer = consumer2;
        if (!encounteredMultiConsumerError) {
            try {
                vertexConsumer = new MergedVertexConsumer(consumer1, consumer2);
            } catch (Exception e) {
                AlexsMobs.LOGGER.warn("Encountered issue mixing two render types together. Likely an issue with Optifine or other rendering mod. This warning will only display once.");
                encounteredMultiConsumerError = true;
            }
        }
        return vertexConsumer;
    }

    /** Replaces removed {@code ItemRenderer#getFoilBuffer} for entity cutouts (e.g. kangaroo armor). */
    public static VertexConsumer entityFoilBuffer(BufferSource buffer, RenderType base, boolean foil) {
        if (!foil) {
            return buffer.getBuffer(base);
        }
        return createMergedVertexConsumer(buffer.getBuffer(base), buffer.getBuffer(RenderTypes.entitySolidGlint(net.minecraft.client.renderer.feature.ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)));
    }

    /** Replaces removed {@code ItemRenderer#getArmorFoilBuffer} for armor cutouts. */
    public static VertexConsumer armorFoilBuffer(BufferSource buffer, RenderType base, boolean foil) {
        if (!foil) {
            return buffer.getBuffer(base);
        }
        return createMergedVertexConsumer(buffer.getBuffer(base), buffer.getBuffer(RenderTypes.trimmedArmorGlint()));
    }
}
