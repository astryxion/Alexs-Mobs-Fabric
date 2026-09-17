package com.github.alexthe666.alexsmobs.client.render.item;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.ItemQuads;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class GhostlyPickaxeItemModel implements ItemModel {

    private static final Field ACTIVE_LAYER_COUNT;
    private static final Field LAYERS;
    private static final Field LAYER_QUADS;

    static {
        try {
            ACTIVE_LAYER_COUNT = ItemStackRenderState.class.getDeclaredField("activeLayerCount");
            ACTIVE_LAYER_COUNT.setAccessible(true);
            LAYERS = ItemStackRenderState.class.getDeclaredField("layers");
            LAYERS.setAccessible(true);
            LAYER_QUADS = ItemStackRenderState.LayerRenderState.class.getDeclaredField("quads");
            LAYER_QUADS.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final ItemModel inner;

    public GhostlyPickaxeItemModel(ItemModel inner) {
        this.inner = inner;
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        this.inner.update(state, stack, resolver, displayContext, level, owner, seed);
        try {
            int n = ACTIVE_LAYER_COUNT.getInt(state);
            ItemStackRenderState.LayerRenderState[] layers = (ItemStackRenderState.LayerRenderState[]) LAYERS.get(state);
            for (int i = 0; i < n; i++) {
                ItemStackRenderState.LayerRenderState layer = layers[i];
                ItemQuads quads = (ItemQuads) LAYER_QUADS.get(layer);
                if (quads == null || quads.isEmpty()) {
                    continue;
                }
                List<BakedQuad> rewritten = new ArrayList<>(quads.all().size());
                for (BakedQuad quad : quads.all()) {
                    rewritten.add(fullbrightGhost(quad));
                }
                layer.setQuads(ItemQuads.split(rewritten));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static BakedQuad fullbrightGhost(BakedQuad quad) {
        BakedQuad.MaterialInfo mi = quad.materialInfo();
        BakedQuad.MaterialInfo newMi = new BakedQuad.MaterialInfo(
                mi.sprite(),
                mi.layer(),
                AMRenderTypes.getGhostPickaxe(mi.sprite().atlasLocation()),
                mi.itemGlintRenderType(),
                mi.itemGlintSpecialRenderType(),
                mi.tintIndex(),
                mi.shadeDirectionOverride(),
                15);
        return new BakedQuad(
                quad.position0(),
                quad.position1(),
                quad.position2(),
                quad.position3(),
                quad.packedUV0(),
                quad.packedUV1(),
                quad.packedUV2(),
                quad.packedUV3(),
                quad.direction(),
                newMi);
    }
}
