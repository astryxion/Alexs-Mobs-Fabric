package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.mixin.client.MinecraftAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.CustomData;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CitadelItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation DEFAULT_ICON_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/icon_default.png");
    private static final Map<String, ResourceLocation> LOADED_ICONS = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private static List mobEffectList = null;

    private static CompoundTag getCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : null;
    }

    public CitadelItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        float partialTicks = ((MinecraftAccessor) Minecraft.getInstance()).citadel$getTimer().getGameTimeDeltaPartialTick(false);
        float ticksExisted = Util.getMillis() / 50F + partialTicks;
        int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
        CompoundTag tag = getCustomTag(stack);
        if (stack.getItem() == Citadel.FANCY_ITEM) {
            Random random = new Random();
            boolean animateAnyways = false;
            ItemStack toRender = null;
            if (tag != null && tag.contains("DisplayItem")) {
                String displayID = tag.getString("DisplayItem");
                toRender = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(displayID)));
                if (tag.contains("DisplayItemNBT")) {
                    try {
                        toRender.set(DataComponents.CUSTOM_DATA, CustomData.of(tag.getCompound("DisplayItemNBT")));
                    } catch (Exception e) {
                        toRender = new ItemStack(Items.BARRIER);
                    }
                }
            }
            if (toRender == null) {
                animateAnyways = true;
                toRender = new ItemStack(Items.BARRIER);
            }
            matrixStack.pushPose();
            matrixStack.translate(0.5F, 0.5f, 0.5f);
            if (tag != null && tag.contains("DisplayShake") && tag.getBoolean("DisplayShake")) {
                matrixStack.translate((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
            }
            if (animateAnyways || tag != null && tag.contains("DisplayBob") && tag.getBoolean("DisplayBob")) {
                matrixStack.translate(0, 0.05F + 0.1F * Mth.sin(0.3F * ticksExisted), 0);
            }
            if (tag != null && tag.contains("DisplaySpin") && tag.getBoolean("DisplaySpin")) {
                matrixStack.mulPose(Axis.YP.rotationDegrees(6 * ticksExisted));
            }
            if (animateAnyways || tag != null && tag.contains("DisplayZoom") && tag.getBoolean("DisplayZoom")) {
                float scale = (float) (1F + 0.15F * (Math.sin(ticksExisted * 0.3F) + 1F));
                matrixStack.scale(scale, scale, scale);
            }
            if (tag != null && tag.contains("DisplayScale") && tag.getFloat("DisplayScale") != 1.0F) {
                float scale = tag.getFloat("DisplayScale");
                matrixStack.scale(scale, scale, scale);
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(toRender, transformType, combinedLight, combinedOverlay, matrixStack, buffer, null, id);
            matrixStack.popPose();
        }
        if (stack.getItem() == Citadel.EFFECT_ITEM) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            // RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            net.minecraft.core.Holder<MobEffect> effect;
            if (tag != null && tag.contains("DisplayEffect")) {
                String displayID = tag.getString("DisplayEffect");
                effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(displayID)).orElse(BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse("minecraft:speed")).orElseThrow());
            } else {
                if (mobEffectList == null) {
                    mobEffectList = BuiltInRegistries.MOB_EFFECT.holders().toList();
                }
                int size = mobEffectList.size();
                int time = (int) (Util.getMillis() / 500);
                effect = (net.minecraft.core.Holder<MobEffect>) mobEffectList.get(time % size);
                if (effect == null) {
                    effect = MobEffects.MOVEMENT_SPEED;
                }
            }
            if (effect == null) {
                effect = MobEffects.MOVEMENT_SPEED;
            }
            MobEffectTextureManager potionspriteuploader = Minecraft.getInstance().getMobEffectTextures();
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 0.5F);
            TextureAtlasSprite sprite = potionspriteuploader.get(effect);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, sprite.atlasLocation());
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            PoseStack.Pose pose = matrixStack.last();
            int br = 255;
            bufferbuilder.addVertex(pose, 1f, 1f, 0f).setUv(sprite.getU1(), sprite.getV0()).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(pose, 0f, 1f, 0f).setUv(sprite.getU0(), sprite.getV0()).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(pose, 0f, 0f, 0f).setUv(sprite.getU0(), sprite.getV1()).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(pose, 1f, 0f, 0f).setUv(sprite.getU1(), sprite.getV1()).setColor(br, br, br, 255).setLight(combinedLight);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            matrixStack.popPose();
        }
        if (stack.getItem() == Citadel.ICON_ITEM) {
            ResourceLocation texture = DEFAULT_ICON_TEXTURE;
            if (tag != null && tag.contains("IconLocation")) {
                String iconLocationStr = tag.getString("IconLocation");
                if (LOADED_ICONS.containsKey(iconLocationStr)) {
                    texture = LOADED_ICONS.get(iconLocationStr);
                } else {
                    texture = ResourceLocation.parse(iconLocationStr);
                    LOADED_ICONS.put(iconLocationStr, texture);
                }
            }
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 0.5F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, texture);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            PoseStack.Pose pose = matrixStack.last();
            int br = 255;
            bufferbuilder.addVertex(pose, 1f, 1f, 0f).setUv(1, 0).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(pose, 0f, 1f, 0f).setUv(0, 0).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(pose, 0f, 0f, 0f).setUv(0, 1).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(pose, 1f, 0f, 0f).setUv(1, 1).setColor(br, br, br, 255).setLight(combinedLight);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            matrixStack.popPose();
        }
    }


}
