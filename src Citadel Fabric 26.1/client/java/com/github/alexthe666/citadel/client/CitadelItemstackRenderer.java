package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.item.CitadelDataComponents;
import com.github.alexthe666.citadel.item.data.FancyItemDisplay;
import com.github.alexthe666.citadel.item.data.IconItemDisplay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Client item model for Citadel's effect, fancy, and icon items (26.1 {@link ItemModel} pipeline).
 */
public final class CitadelItemstackRenderer implements ItemModel {

    public static final class Unbaked implements ItemModel.Unbaked {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        private Unbaked() {
        }

        @Override
        public ItemModel bake(BakingContext context, org.joml.Matrix4fc transformation) {
            return new CitadelItemstackRenderer(new CitadelStackSpecialRenderer(context.sprites()));
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
        }

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return MAP_CODEC;
        }
    }

    private static final Identifier DEFAULT_ICON_TEXTURE = Identifier.parse("citadel:textures/gui/book/icon_default.png");
    private static final Map<String, Identifier> LOADED_ICONS = new HashMap<>();
    private static List<Holder.Reference<MobEffect>> mobEffectList = null;

    private final CitadelStackSpecialRenderer stackSpecialRenderer;

    private CitadelItemstackRenderer(CitadelStackSpecialRenderer stackSpecialRenderer) {
        this.stackSpecialRenderer = stackSpecialRenderer;
    }

    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner itemOwner, int seed) {
        renderState.appendModelIdentityElement(this);
        if (stack.getItem() == Citadel.FANCY_ITEM) {
            updateFancy(renderState, stack, resolver, displayContext, level, itemOwner, seed);
        } else if (stack.getItem() == Citadel.EFFECT_ITEM || stack.getItem() == Citadel.ICON_ITEM) {
            ItemStackRenderState.LayerRenderState layer = renderState.newLayer();
            layer.setupSpecialModel(this.stackSpecialRenderer, stack);
        }
    }

    private void updateFancy(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner itemOwner, int seed) {
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();
        float ticksExisted = Util.getMillis() / 50F + partialTicks;
        Random random = new Random();
        FancyItemDisplay display = stack.get(CitadelDataComponents.FANCY_ITEM_DISPLAY);
        ItemStack toRender = null;
        if (display != null && !display.displayItem().isEmpty()) {
            toRender = BuiltInRegistries.ITEM.get(Identifier.parse(display.displayItem()))
                    .map(h -> new ItemStack(h, 1))
                    .orElse(ItemStack.EMPTY);
        }
        boolean animateAnyways = false;
        if (toRender == null || toRender.isEmpty()) {
            animateAnyways = true;
            toRender = new ItemStack(Items.BARRIER);
        }
        Matrix4f local = new Matrix4f();
        local.translate(0.5F, 0.5f, 0.5f);
        if (display != null && display.displayShake()) {
            local.translate((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
        }
        if (animateAnyways || (display != null && display.displayBob())) {
            local.translate(0, 0.05F + 0.1F * Mth.sin(0.3F * ticksExisted), 0);
        }
        if (display != null && display.displaySpin()) {
            local.rotateY((float) Math.toRadians(6 * ticksExisted));
        }
        if (animateAnyways || (display != null && display.displayZoom())) {
            float scale = (float) (1F + 0.15F * (Math.sin(ticksExisted * 0.3F) + 1F));
            local.scale(scale, scale, scale);
        }
        if (display != null && display.displayScale() != 1.0F) {
            float scale = display.displayScale();
            local.scale(scale, scale, scale);
        }
        if (display != null && (display.displayShake() || display.displayBob() || display.displaySpin() || display.displayZoom())) {
            renderState.setAnimated();
        }
        int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
        ItemStackRenderState.LayerRenderState layer = renderState.newLayer();
        layer.setLocalTransform(local);
        resolver.appendItemLayers(renderState, toRender, displayContext, level, itemOwner, id);
    }

    private static final class CitadelStackSpecialRenderer implements SpecialModelRenderer<ItemStack> {
        private final SpriteGetter spriteGetter;

        private CitadelStackSpecialRenderer(SpriteGetter spriteGetter) {
            this.spriteGetter = spriteGetter;
        }

        @Override
        public ItemStack extractArgument(ItemStack stack) {
            return stack;
        }

        @Override
        public void submit(ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean p5, int p6) {
            if (stack.getItem() == Citadel.EFFECT_ITEM) {
                submitEffect(stack, poseStack, collector, packedLight);
            } else if (stack.getItem() == Citadel.ICON_ITEM) {
                submitIcon(stack, poseStack, collector, packedLight);
            }
        }

        private void submitEffect(ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
            if (mobEffectList == null) {
                mobEffectList = BuiltInRegistries.MOB_EFFECT.listElements().toList();
            }
            int size = mobEffectList.size();
            int time = (int) (Util.getMillis() / 500);
            Holder<MobEffect> effect = mobEffectList.get(time % size);
            if (effect == null && !mobEffectList.isEmpty()) {
                effect = mobEffectList.getFirst();
            }
            TextureAtlasSprite sprite = mobEffectSprite(effect);
            if (sprite == null) {
                return;
            }
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.5F);
            drawSpriteQuad(poseStack, collector, sprite, packedLight);
            poseStack.popPose();
        }

        private void submitIcon(ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
            Identifier texture = DEFAULT_ICON_TEXTURE;
            IconItemDisplay display = stack.get(CitadelDataComponents.ICON_ITEM_DISPLAY);
            if (display != null && !display.iconLocation().isEmpty()) {
                String iconLocationStr = display.iconLocation();
                texture = LOADED_ICONS.computeIfAbsent(iconLocationStr, Identifier::parse);
            }
            TextureAtlasSprite sprite = trySprite(TextureAtlas.LOCATION_ITEMS, texture);
            if (sprite == null) {
                sprite = trySprite(TextureAtlas.LOCATION_BLOCKS, texture);
            }
            if (sprite == null) {
                return;
            }
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.5F);
            drawSpriteQuad(poseStack, collector, sprite, packedLight);
            poseStack.popPose();
        }

        private @Nullable TextureAtlasSprite mobEffectSprite(Holder<MobEffect> effect) {
            Identifier key = BuiltInRegistries.MOB_EFFECT.getKey(effect.value());
            if (key == null) {
                return null;
            }
            Identifier tex = Identifier.fromNamespaceAndPath(key.getNamespace(), "mob_effect/" + key.getPath());
            return trySprite(TextureAtlas.LOCATION_ITEMS, tex);
        }

        private @Nullable TextureAtlasSprite trySprite(Identifier atlas, Identifier texture) {
            try {
                return spriteGetter.get(new SpriteId(atlas, texture));
            } catch (Exception e) {
                return null;
            }
        }

        private static void drawSpriteQuad(PoseStack poseStack, SubmitNodeCollector collector, TextureAtlasSprite sprite, int packedLight) {
            var renderType = RenderTypes.entityCutout(sprite.atlasLocation());
            collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                Matrix4f mx = pose.pose();
                int br = 255;
                buffer.addVertex(mx, 1, 1, 0).setUv(sprite.getU1(), sprite.getV0()).setColor(br, br, br, 255).setLight(packedLight);
                buffer.addVertex(mx, 0, 1, 0).setUv(sprite.getU0(), sprite.getV0()).setColor(br, br, br, 255).setLight(packedLight);
                buffer.addVertex(mx, 0, 0, 0).setUv(sprite.getU0(), sprite.getV1()).setColor(br, br, br, 255).setLight(packedLight);
                buffer.addVertex(mx, 1, 0, 0).setUv(sprite.getU1(), sprite.getV1()).setColor(br, br, br, 255).setLight(packedLight);
            });
        }

        @Override
        public void getExtents(Consumer<org.joml.Vector3fc> consumer) {
            consumer.accept(new Vector3f(0, 0, 0));
            consumer.accept(new Vector3f(1, 1, 1));
        }
    }
}
