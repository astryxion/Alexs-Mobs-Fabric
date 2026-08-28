package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.TextureAtlas;
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

    private static RenderType glintType(String name, Identifier texture, TextureTransform transform) {
        RenderSetup setup = RenderSetup.builder(RenderPipelines.GLINT)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .setTextureTransform(transform)
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
        return RenderType.create(name, setup);
    }

    public static final RenderType COMBJELLY_RAINBOW_GLINT = Util.make(() -> {
        RenderSetup setup = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
                .withTexture("Sampler0", Identifier.parse("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_rainbow.png"))
                .useLightmap()
                .useOverlay()
                .setTextureTransform(COMB_JELLY_TEXTURING)
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
        return RenderType.create("cj_rainbow_glint", setup);
    });

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

    private static final com.mojang.blaze3d.pipeline.RenderPipeline EYES_SHADER = RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE;

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

    public static RenderType getSkulkBoom() {
        return RenderTypes.energySwirl(Identifier.parse("alexsmobs:textures/particle/skulk_boom.png"), 0.0F, 0.0F);
    }

    /**
     * Same shader path as {@link RenderPipelines#ENERGY_SWIRL} / {@link RenderTypes#energySwirl}, but with
     * {@link BlendFunction#TRANSLUCENT} like 1.21.1 {@code getUnderminer} ({@code TRANSLUCENT_TRANSPARENCY} + energy swirl
     * shader). Vanilla {@code energySwirl} uses {@link BlendFunction#ADDITIVE}, which blows out entity skin quads to white.
     */
    public static final RenderPipeline UNDERMINER_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
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
            .build());

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
        return RenderTypes.entityTranslucent(texture);
    }

    /**
     * Item shader + lightning blend for GUI / hand (flat item quads). Entity shader on item geometry renders black in 26.2.
     */
    public static final RenderPipeline GHOST_PICKAXE_ITEM_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.ITEM_SNIPPET)
            .withLocation(Identifier.parse("alexsmobs:pipeline/ghost_pickaxe"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .build());

    /**
     * Entity shader + lightning blend for dropped item entities / item frames — same intent as 1.20
     * {@code RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER} with {@link BlendFunction#LIGHTNING}.
     */
    public static final RenderPipeline GHOST_PICKAXE_ENTITY_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(Identifier.parse("alexsmobs:pipeline/ghost_pickaxe_entity"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .build());

    private static RenderSetup buildGhostPickaxeSetup(RenderPipeline pipeline, boolean itemEntityTarget) {
        var builder = RenderSetup.builder(pipeline)
                .withTexture("Sampler0", TextureAtlas.LOCATION_ITEMS)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE);
        if (itemEntityTarget) {
            builder.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET);
        }
        return builder.createRenderSetup();
    }

    private static final RenderType GHOST_PICKAXE_TYPE = Util.make(
            () -> RenderType.create("ghost_pickaxe", buildGhostPickaxeSetup(GHOST_PICKAXE_ITEM_PIPELINE, false)));

    private static final RenderType GHOST_PICKAXE_ITEM_ENTITY_TYPE = Util.make(
            () -> RenderType.create("ghost_pickaxe_item_entity", buildGhostPickaxeSetup(GHOST_PICKAXE_ENTITY_PIPELINE, true)));

    /** GUI, hand, and other non-world-item contexts — uses the main framebuffer. */
    public static RenderType getGhostPickaxe() {
        return GHOST_PICKAXE_TYPE;
    }

    /** Dropped item entities and item frames — same as 1.20 {@code ITEM_ENTITY_TARGET}. */
    public static RenderType getGhostPickaxeItemEntity() {
        return GHOST_PICKAXE_ITEM_ENTITY_TYPE;
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

    public static VertexConsumer createMergedVertexConsumer(VertexConsumer consumer1, VertexConsumer consumer2) {
        return new MergedVertexConsumer(consumer1, consumer2);
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
        public VertexConsumer setColor(int color) {
            this.first.setColor(color);
            this.second.setColor(color);
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            this.first.setColor(r, g, b, a);
            this.second.setColor(r, g, b, a);
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

    /** Replaces removed {@code ItemRenderer#getFoilBuffer} for entity cutouts (e.g. kangaroo armor). */
    public static void submitEntityFoilGeometry(SubmitNodeCollector collector, com.mojang.blaze3d.vertex.PoseStack poseStack, RenderType base, boolean foil, SubmitNodeCollector.CustomGeometryRenderer renderer) {
        collector.submitCustomGeometry(poseStack, base, renderer);
        if (foil) {
            collector.submitCustomGeometry(poseStack, RenderTypes.entityGlint(), renderer);
        }
    }

    /** Replaces removed {@code ItemRenderer#getArmorFoilBuffer} for armor cutouts. */
    public static void submitArmorFoilGeometry(SubmitNodeCollector collector, com.mojang.blaze3d.vertex.PoseStack poseStack, RenderType base, boolean foil, SubmitNodeCollector.CustomGeometryRenderer renderer) {
        collector.submitCustomGeometry(poseStack, base, renderer);
        if (foil) {
            collector.submitCustomGeometry(poseStack, RenderTypes.armorEntityGlint(), renderer);
        }
    }
}
