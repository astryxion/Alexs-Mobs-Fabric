package com.github.alexthe666.citadel.client.model.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Baked Tabula geometry for block rendering. The Tabula {@link VanillaTabulaModel} bake pipeline is
 * currently disabled in-source; when re-enabled it must emit {@link BakedQuad} in the 26.1 packed format.
 */
public class BakedTabulaModel extends SingleVariant {
    private final ImmutableMap<ItemDisplayContext, Transformation> transforms;

    public BakedTabulaModel(
            ImmutableList<BakedQuad> quads,
            TextureAtlasSprite particleSprite,
            ImmutableMap<ItemDisplayContext, Transformation> transforms) {
        super(new TabulaModelPart(quads, particleSprite));
        this.transforms = transforms;
    }

    public ImmutableMap<ItemDisplayContext, Transformation> getTransforms() {
        return this.transforms;
    }

    private static final class TabulaModelPart implements BlockStateModelPart {
        private final ImmutableList<BakedQuad> quads;
        private final Material.Baked particleMaterial;
        private final int materialFlags;

        TabulaModelPart(ImmutableList<BakedQuad> quads, TextureAtlasSprite particleSprite) {
            this.quads = quads;
            this.particleMaterial = new Material.Baked(particleSprite, false);
            int flags = 0;
            for (BakedQuad q : quads) {
                flags |= q.materialInfo().flags();
            }
            this.materialFlags = flags;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            if (direction == null) {
                return this.quads;
            }
            List<BakedQuad> out = new ArrayList<>();
            for (BakedQuad q : this.quads) {
                if (q.direction() == direction) {
                    out.add(q);
                }
            }
            return out;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.particleMaterial;
        }

        @Override
        public int materialFlags() {
            return this.materialFlags;
        }
    }
}
