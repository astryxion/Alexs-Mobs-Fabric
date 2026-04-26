package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.model.*;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemStinkRay;
import com.github.alexthe666.alexsmobs.item.ItemTabIcon;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

/**
 * Special item rendering for 26.1: {@link ItemModel} + {@link SpecialModelRenderer} (replaces BEWLR).
 */
public final class AMItemstackRenderer implements SpecialModelRenderer<AMItemstackRenderer.AmSpecialItemPayload> {

    /**
     * Carries the item stack and the {@link ItemDisplayContext} from {@link SpecialItemModel#update} into
     * {@link #submit} without a {@link ThreadLocal}. ThreadLocals break here because multiple items can call
     * {@code ItemModel#update} before any {@code submit} runs (different hands, entities, GUI), so queued contexts
     * were paired with the wrong submit.
     *
     * @param itemOwner holder being rendered (e.g. shield blocking for that entity); may be {@code null}
     */
    public record AmSpecialItemPayload(ItemStack stack, ItemDisplayContext displayContext, @Nullable ItemOwner itemOwner) {}

    public static final List<String> ISTER_ITEM_MODEL_PATHS = List.of(
            "shield_of_the_deep",
            "shield_of_the_deep_blocking",
            "mysterious_worm",
            "falconry_glove",
            "vine_lasso",
            "skelewag_sword",
            "tab_icon",
            "shattered_dimensional_carver",
            "stink_ray",
            "transmutation_table");

    public static int ticksExisted = 0;

    public static final AMItemstackRenderer INSTANCE = new AMItemstackRenderer();

    private AMItemstackRenderer() {
    }

    private static final ModelShieldOfTheDeep SHIELD_OF_THE_DEEP_MODEL = new ModelShieldOfTheDeep();
    private static final Identifier SHIELD_OF_THE_DEEP_TEXTURE = Identifier.parse("alexsmobs:textures/armor/shield_of_the_deep.png");
    /** From {@code assets/alexsmobs/models/item/shield_of_the_deep.json}; translations in block space (JSON / 16). */
    private static final ItemTransform SHIELD_OF_THE_DEEP_GUI = new ItemTransform(
            new Vector3f(16.0F, -141.0F, 0.0F),
            new Vector3f(0.0F, 2.25F / 16.0F, 0.0F),
            new Vector3f(0.9F, 0.9F, 0.9F));
    private static final ItemTransform SHIELD_OF_THE_DEEP_GROUND = new ItemTransform(
            new Vector3f(0.0F, 0.0F, 0.0F),
            new Vector3f(0.0F, 1.25F / 16.0F, 0.0F),
            new Vector3f(0.5F, 0.5F, 0.5F));
    private static final ItemTransform SHIELD_OF_THE_DEEP_HEAD = new ItemTransform(
            new Vector3f(0.0F, 90.0F, 0.0F),
            new Vector3f(0.0F, 2.25F / 16.0F, -1.25F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    private static final ItemTransform SHIELD_OF_THE_DEEP_FIXED = new ItemTransform(
            new Vector3f(0.0F, 90.0F, 0.0F),
            new Vector3f(0.0F, 2.0F / 16.0F, 0.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    /** Idle {@code shield_of_the_deep.json} {@code thirdperson_righthand}. */
    private static final ItemTransform SHIELD_OF_THE_DEEP_IDLE_THIRD_PERSON_RIGHT_HAND = new ItemTransform(
            new Vector3f(0.0F, 0.0F, 0.0F),
            new Vector3f(2.5F / 16.0F, 0.0F, 2.75F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    /** Idle {@code shield_of_the_deep.json} {@code thirdperson_lefthand}. */
    private static final ItemTransform SHIELD_OF_THE_DEEP_IDLE_THIRD_PERSON_LEFT_HAND = new ItemTransform(
            new Vector3f(0.0F, 180.0F, 0.0F),
            new Vector3f(2.5F / 16.0F, 0.0F, 2.75F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    /** Blocking {@code shield_of_the_deep_blocking.json} hand transforms (item model override while using). */
    private static final ItemTransform SHIELD_OF_THE_DEEP_BLOCKING_THIRD_PERSON_RIGHT_HAND = new ItemTransform(
            new Vector3f(45.0F, 55.0F, 0.0F),
            new Vector3f(1.5F / 16.0F, 0.0F, 1.0F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    private static final ItemTransform SHIELD_OF_THE_DEEP_BLOCKING_THIRD_PERSON_LEFT_HAND = new ItemTransform(
            new Vector3f(45.0F, -135.0F, 0.0F),
            new Vector3f(1.5F / 16.0F, 0.0F, 1.0F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    /** From 1.21.1 {@code shield_of_the_deep_blocking.json} (flat shield FP pose, not vanilla wooden shield). */
    private static final ItemTransform SHIELD_OF_THE_DEEP_BLOCKING_FIRST_PERSON_RIGHT_HAND = new ItemTransform(
            new Vector3f(20.0F, 89.0F, 0.0F),
            new Vector3f(2.0F / 16.0F, 5.0F / 16.0F, 2.0F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    private static final ItemTransform SHIELD_OF_THE_DEEP_BLOCKING_FIRST_PERSON_LEFT_HAND = new ItemTransform(
            new Vector3f(20.0F, -90.0F, 0.0F),
            new Vector3f(0.0F, 5.0F / 16.0F, 2.0F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    private static final ItemTransform SHIELD_OF_THE_DEEP_FIRST_PERSON_RIGHT_HAND = new ItemTransform(
            new Vector3f(0.0F, 89.0F, 0.0F),
            new Vector3f(2.0F / 16.0F, -1.0F / 16.0F, 0.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    private static final ItemTransform SHIELD_OF_THE_DEEP_FIRST_PERSON_LEFT_HAND = new ItemTransform(
            new Vector3f(0.0F, -90.0F, 0.0F),
            new Vector3f(0.0F, -1.25F / 16.0F, 0.5F / 16.0F),
            new Vector3f(1.0F, 1.0F, 1.0F));
    private static final ModelMysteriousWorm MYTERIOUS_WORM_MODEL = new ModelMysteriousWorm();
    private static final Identifier MYTERIOUS_WORM_TEXTURE = Identifier.parse("alexsmobs:textures/item/mysterious_worm_model.png");
    private static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();
    private static final Identifier ANCHOR_TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor.png");
    private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
    private static final Identifier WINCH_TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_winch.png");
    private static final ModelEndPirateShipWheel SHIP_WHEEL_MODEL = new ModelEndPirateShipWheel();
    private static final Identifier SHIP_WHEEL_TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/ship_wheel.png");
    private static final Identifier TRANSMUTATION_TABLE_TEXTURE = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table.png");
    private static final Identifier TRANSMUTATION_TABLE_GLOW_TEXTURE = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table_glow.png");
    private static final Identifier TRANSMUTATION_TABLE_OVERLAY = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table_overlay.png");
    private static final ModelTransmutationTable TRANSMUTATION_TABLE_MODEL = new ModelTransmutationTable(0F);
    private static final ModelTransmutationTable TRANSMUTATION_TABLE_OVERLAY_MODEL = new ModelTransmutationTable(0.01F);
    /**
     * Same values as {@code assets/alexsmobs/models/item/transmutation_table.json} hand display;
     * translation components are in block space (JSON pixels / 16).
     * For in-hand contexts, apply only after undoing {@link ItemTransform#NO_TRANSFORM} (see hand branch below).
     */
    private static final ItemTransform TRANSMUTATION_TABLE_THIRD_PERSON_HAND = new ItemTransform(
            new Vector3f(75.0F, 315.0F, 0.0F),
            new Vector3f(-2.0F / 16.0F, 2.5F / 16.0F, 0.0F),
            new Vector3f(0.375F, 0.375F, 0.375F));
    private static final ItemTransform TRANSMUTATION_TABLE_FIRST_PERSON_HAND = new ItemTransform(
            new Vector3f(0.0F, 315.0F, 0.0F),
            new Vector3f(-2.0F / 16.0F, 2.5F / 16.0F, 0.0F),
            new Vector3f(0.375F, 0.375F, 0.375F));
    /** Matches {@code transmutation_table.json} {@code display.gui} (rotation, scale, and built-in slot anchor). */
    private static final ItemTransform TRANSMUTATION_TABLE_GUI = new ItemTransform(
            new Vector3f(30.0F, 45.0F, 0.0F),
            new Vector3f(0.0F, 0.0F, 0.0F),
            new Vector3f(0.75F, 0.75F, 0.75F));
    /** Values from {@code assets/alexsmobs/models/item/mysterious_worm.json}; translations in block space (JSON / 16). */
    private static final ItemTransform MYSTERIOUS_WORM_GUI = new ItemTransform(
            new Vector3f(30.0F, 45.0F, 0.0F),
            new Vector3f(10.0F / 16.0F, 11.0F / 16.0F, 0.0F),
            new Vector3f(0.8F, 0.8F, 0.8F));
    private static final ItemTransform MYSTERIOUS_WORM_GROUND = new ItemTransform(
            new Vector3f(0.0F, 0.0F, 0.0F),
            new Vector3f(3.0F / 16.0F, 8.0F / 16.0F, 5.0F / 16.0F),
            new Vector3f(0.5F, 0.5F, 0.5F));
    private static final ItemTransform MYSTERIOUS_WORM_HEAD = new ItemTransform(
            new Vector3f(0.0F, 180.0F, 0.0F),
            new Vector3f(-15.0F / 16.0F, 54.0F / 16.0F, 5.0F / 16.0F),
            new Vector3f(2.0F, 2.0F, 2.0F));
    private static final ItemTransform MYSTERIOUS_WORM_FIXED = new ItemTransform(
            new Vector3f(0.0F, 270.0F, 0.0F),
            new Vector3f(-6.0F / 16.0F, 14.0F / 16.0F, 4.0F / 16.0F),
            new Vector3f(0.8F, 0.8F, 0.8F));
    /** {@code thirdperson_righthand}; pass {@code leftHand} to {@link ItemTransform#apply}. */
    private static final ItemTransform MYSTERIOUS_WORM_THIRD_PERSON_HAND = new ItemTransform(
            new Vector3f(75.0F, 180.0F, 0.0F),
            new Vector3f(-5.0F / 16.0F, 5.0F / 16.0F, 10.0F / 16.0F),
            new Vector3f(0.6F, 0.6F, 0.6F));
    private static final ItemTransform MYSTERIOUS_WORM_FIRST_PERSON_HAND = new ItemTransform(
            new Vector3f(0.0F, 220.0F, 0.0F),
            new Vector3f(3.0F / 16.0F, 15.0F / 16.0F, -9.0F / 16.0F),
            new Vector3f(0.8F, 0.8F, 0.8F));
    private static List<ItemStack> DIMENSIONAL_CARVER_SHARDS;

    private final Map<String, Entity> renderedEntites = new HashMap<>();
    private final List<EntityType<?>> blockedRenderEntities = new ArrayList<>();
    /** Citadel {@link ModelShieldOfTheDeep} draws from {@link PoseStack#last()}; must match {@code submitCustomGeometry} pose at replay. */
    private final PoseStack citadelPoseScratch = new PoseStack();

    public static void incrementTick() {
        ticksExisted++;
    }

    /**
     * Matches 1.21.1 {@code ItemProperties.register(..., "blocking")}: {@code getUseItem() == stack}.
     * Also accepts {@link ItemStack#isSameItemSameComponents} when the render stack is not the same instance as the live hand stack.
     */
    private static boolean isShieldOfTheDeepBlocking(ItemStack stack, @Nullable ItemOwner owner) {
        if (!stack.is(AMItemRegistry.SHIELD_OF_THE_DEEP)) {
            return false;
        }
        if (owner != null) {
            LivingEntity living = owner.asLivingEntity();
            if (living != null && living.isUsingItem()) {
                ItemStack inUse = living.getUseItem();
                if (inUse == stack || ItemStack.isSameItemSameComponents(inUse, stack)) {
                    return true;
                }
            }
        }
        Player player = Minecraft.getInstance().player;
        if (player == null || !player.isUsingItem()) {
            return false;
        }
        ItemStack inUse = player.getUseItem();
        return inUse == stack || ItemStack.isSameItemSameComponents(inUse, stack);
    }

    private static float getScaleFor(EntityType<?> type, List<Pair<EntityType<?>, Float>> mobIcons) {
        for (Pair<EntityType<?>, Float> pair : mobIcons) {
            if (pair.getFirst() == type) {
                return pair.getSecond();
            }
        }
        return 1.0F;
    }

    private static List<ItemStack> getDimensionalCarverShards() {
        if (DIMENSIONAL_CARVER_SHARDS == null || DIMENSIONAL_CARVER_SHARDS.isEmpty()) {
            DIMENSIONAL_CARVER_SHARDS = Util.make(Lists.newArrayList(), (list) -> {
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_0")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_1")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_2")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_3")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_4")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_5")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_6")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_7")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_8")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_9")).map(Holder::value).orElse(Items.AIR)));
                list.add(new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse("alexsmobs:dimensional_carver_shard_10")).map(Holder::value).orElse(Items.AIR)));
            });
        }
        return DIMENSIONAL_CARVER_SHARDS;
    }

    public static void drawEntityOnScreen(PoseStack matrixstack, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity, SubmitNodeCollector collector) {
        float f = (float) Math.atan(-mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        matrixstack.scale(scale, scale, scale);
        entity.setOnGround(false);
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(20.0F);
        float partialTicksForRender = Minecraft.getInstance().isPaused() || entity instanceof EntityMimicOctopus ? 0 : partialTicks;
        int tick;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = Minecraft.getInstance().player.tickCount;
        }
        if (follow) {
            float yaw = f * 45.0F;
            entity.setYRot(yaw);
            entity.tickCount = tick;
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).yBodyRot = yaw;
                ((LivingEntity) entity).yBodyRotO = yaw;
                ((LivingEntity) entity).yHeadRot = yaw;
                ((LivingEntity) entity).yHeadRotO = yaw;
            }

            quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
            quaternion.mul(quaternion1);
        }

        matrixstack.mulPose(quaternion);
        matrixstack.mulPose(Axis.XP.rotationDegrees((float) (-xRot)));
        matrixstack.mulPose(Axis.YP.rotationDegrees((float) yRot));
        matrixstack.mulPose(Axis.ZP.rotationDegrees((float) zRot));
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderState state = entityrenderdispatcher.extractEntity(entity, partialTicksForRender);
        CameraRenderState cam = ClientProxy.lastCameraRenderState != null ? ClientProxy.lastCameraRenderState : new CameraRenderState();
        entityrenderdispatcher.submit(state, cam, 0.0D, 0.0D, 0.0D, matrixstack, collector);
        entity.setYRot(0.0F);
        entity.setXRot(0.0F);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).yBodyRot = 0.0F;
            ((LivingEntity) entity).yHeadRotO = 0.0F;
            ((LivingEntity) entity).yHeadRot = 0.0F;
        }
    }

    @Override
    public AmSpecialItemPayload extractArgument(ItemStack stack) {
        return new AmSpecialItemPayload(stack, ItemDisplayContext.NONE, null);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(-0.5F, 0.0F, -0.5F));
        consumer.accept(new Vector3f(0.5F, 1.0F, 0.5F));
    }

    @Override
    public void submit(AmSpecialItemPayload payload, PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int combinedLightIn, int combinedOverlayIn, boolean fabulous, int seed) {
        ItemStack stack = payload.stack();
        ItemDisplayContext transformType = payload.displayContext();
        int tick;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = Minecraft.getInstance().player.tickCount;
        }
        Level level = Minecraft.getInstance().level;
        ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();

        if (stack.getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP) {
            boolean blocking = isShieldOfTheDeepBlocking(stack, payload.itemOwner());
            matrixStackIn.pushPose();
            if (transformType == ItemDisplayContext.GUI) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                SHIELD_OF_THE_DEEP_GUI.apply(transformType.leftHand(), matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.GROUND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                SHIELD_OF_THE_DEEP_GROUND.apply(false, matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.FIXED) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                SHIELD_OF_THE_DEEP_FIXED.apply(false, matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.HEAD) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                SHIELD_OF_THE_DEEP_HEAD.apply(false, matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                if (blocking) {
                    SHIELD_OF_THE_DEEP_BLOCKING_THIRD_PERSON_RIGHT_HAND.apply(false, matrixStackIn.last());
                } else {
                    SHIELD_OF_THE_DEEP_IDLE_THIRD_PERSON_RIGHT_HAND.apply(false, matrixStackIn.last());
                }
            } else if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                if (blocking) {
                    SHIELD_OF_THE_DEEP_BLOCKING_THIRD_PERSON_LEFT_HAND.apply(false, matrixStackIn.last());
                } else {
                    SHIELD_OF_THE_DEEP_IDLE_THIRD_PERSON_LEFT_HAND.apply(false, matrixStackIn.last());
                }
            } else if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                if (blocking) {
                    SHIELD_OF_THE_DEEP_BLOCKING_FIRST_PERSON_RIGHT_HAND.apply(false, matrixStackIn.last());
                } else {
                    SHIELD_OF_THE_DEEP_FIRST_PERSON_RIGHT_HAND.apply(false, matrixStackIn.last());
                }
            } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                if (blocking) {
                    SHIELD_OF_THE_DEEP_BLOCKING_FIRST_PERSON_LEFT_HAND.apply(false, matrixStackIn.last());
                } else {
                    SHIELD_OF_THE_DEEP_FIRST_PERSON_LEFT_HAND.apply(false, matrixStackIn.last());
                }
            }
            matrixStackIn.translate(0.4F, -0.75F, 0.5F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180));
            RenderType cutout = AMRenderTypes.entityCutoutNoCull(SHIELD_OF_THE_DEEP_TEXTURE);
            bufferIn.submitCustomGeometry(matrixStackIn, cutout, (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                    SHIELD_OF_THE_DEEP_MODEL.renderToBuffer(s, consumer, combinedLightIn, combinedOverlayIn, -1)));
            if (stack.hasFoil()) {
                bufferIn.submitCustomGeometry(matrixStackIn, RenderTypes.entityGlint(), (pose, consumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                        SHIELD_OF_THE_DEEP_MODEL.renderToBuffer(s, consumer, combinedLightIn, combinedOverlayIn, -1)));
            }
            matrixStackIn.popPose();
        }
        if (stack.getItem() == AMItemRegistry.MYSTERIOUS_WORM) {
            matrixStackIn.pushPose();
            // Same pattern as transmutation table: layer applies NO_TRANSFORM; re-apply JSON display via ItemTransform.
            if (transformType == ItemDisplayContext.GUI) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                MYSTERIOUS_WORM_GUI.apply(transformType.leftHand(), matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.GROUND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                MYSTERIOUS_WORM_GROUND.apply(false, matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.FIXED) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                MYSTERIOUS_WORM_FIXED.apply(false, matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.HEAD) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                MYSTERIOUS_WORM_HEAD.apply(false, matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                    || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                MYSTERIOUS_WORM_THIRD_PERSON_HAND.apply(
                        transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                        matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                    || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                MYSTERIOUS_WORM_FIRST_PERSON_HAND.apply(
                        transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                        matrixStackIn.last());
            }
            matrixStackIn.translate(0, -2F, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180));
            MYTERIOUS_WORM_MODEL.animateStack(stack);
            bufferIn.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(MYTERIOUS_WORM_TEXTURE), (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                    MYTERIOUS_WORM_MODEL.renderToBuffer(s, consumer, combinedLightIn, combinedOverlayIn, -1)));
            matrixStackIn.popPose();
        }
        if (stack.getItem() == AMItemRegistry.FALCONRY_GLOVE) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                submitChildItem(itemModelResolver, new ItemStack(AMItemRegistry.FALCONRY_GLOVE_HAND), transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            } else {
                submitChildItem(itemModelResolver, new ItemStack(AMItemRegistry.FALCONRY_GLOVE_INVENTORY), transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            }
        }
        if (stack.getItem() == AMItemRegistry.VINE_LASSO) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                Player player = Minecraft.getInstance().player;
                boolean usingLasso = player != null
                        && player.isUsingItem()
                        && player.getUseItem().is(AMItemRegistry.VINE_LASSO);
                if (usingLasso) {
                    if (transformType.firstPerson()) {
                        matrixStackIn.translate(transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.0f, -0.5f);
                    }
                    matrixStackIn.mulPose(Axis.YP.rotation(tick + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)));
                }
                submitChildItem(itemModelResolver, new ItemStack(AMItemRegistry.VINE_LASSO_HAND), transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            } else {
                submitChildItem(itemModelResolver, new ItemStack(AMItemRegistry.VINE_LASSO_INVENTORY), transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            }
        }
        if (stack.getItem() == AMItemRegistry.SKELEWAG_SWORD) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(AMItemRegistry.SKELEWAG_SWORD_INVENTORY);
            ItemStack handItem = new ItemStack(AMItemRegistry.SKELEWAG_SWORD_HAND);
            net.minecraft.world.item.component.CustomData customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
            if (!customData.isEmpty()) {
                spriteItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, customData);
                handItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, customData);
            }
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                submitChildItem(itemModelResolver, handItem, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            } else {
                submitChildItem(itemModelResolver, spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            }
        }
        if (stack.getItem() == AMBlockRegistry.TRANSMUTATION_TABLE.asItem()) {
            matrixStackIn.pushPose();
            // 26.1 SpecialModelRenderer path bypasses baked-model display transforms; re-apply JSON display transforms here.
            // Matches `assets/alexsmobs/models/item/transmutation_table.json`.
            if (transformType == ItemDisplayContext.GUI) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                TRANSMUTATION_TABLE_GUI.apply(transformType.leftHand(), matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.GROUND) {
                matrixStackIn.translate(0.0F, 3.0F / 16.0F, 0.0F);
                matrixStackIn.scale(0.25F, 0.25F, 0.25F);
            } else if (transformType == ItemDisplayContext.FIXED) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            } else if (transformType == ItemDisplayContext.HEAD) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            } else if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                    || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                // Layer always applies NO_TRANSFORM first (-0.5,-0.5,-0.5). Full hand ItemTransform ends with the same
                // corner offset, so without this undo the display transform is composed twice and tears the model apart.
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                boolean leftHand = transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                TRANSMUTATION_TABLE_THIRD_PERSON_HAND.apply(leftHand, matrixStackIn.last());
            } else if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                    || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                matrixStackIn.translate(0.5F, 0.5F, 0.5F);
                boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
                TRANSMUTATION_TABLE_FIRST_PERSON_HAND.apply(leftHand, matrixStackIn.last());
            }
            matrixStackIn.translate(0.5F, 1.6F, 0.5F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
            TRANSMUTATION_TABLE_MODEL.resetToDefaultPose();
            bufferIn.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TRANSMUTATION_TABLE_TEXTURE), (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                    TRANSMUTATION_TABLE_MODEL.renderToBuffer(s, consumer, combinedLightIn, combinedOverlayIn, -1)));
            bufferIn.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucentEmissive(TRANSMUTATION_TABLE_GLOW_TEXTURE), (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                    TRANSMUTATION_TABLE_MODEL.renderToBuffer(s, consumer, combinedLightIn, combinedOverlayIn, -1)));
            TRANSMUTATION_TABLE_OVERLAY_MODEL.resetToDefaultPose();
            bufferIn.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TRANSMUTATION_TABLE_OVERLAY), (pose, consumer) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, this.citadelPoseScratch, s ->
                    TRANSMUTATION_TABLE_OVERLAY_MODEL.renderToBuffer(s, consumer, combinedLightIn, combinedOverlayIn, -1)));
            matrixStackIn.popPose();
        }
        if (stack.getItem() == AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            float f = tick + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            List<ItemStack> shards = getDimensionalCarverShards();
            matrixStackIn.pushPose();
            if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                matrixStackIn.translate(-0.2F, 0, 0);
                matrixStackIn.scale(1.3F, 1.3F, 1.3F);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(60));
            }
            for (int i = 0; i < shards.size(); i++) {
                matrixStackIn.pushPose();
                ItemStack shard = shards.get(i);
                matrixStackIn.translate((float) Math.sin(f * 0.15F + i * 1F) * 0.035F, -(float) Math.cos(f * 0.15F + i * 1F) * 0.035F, (float) Math.cos(f * 0.15F + i * 0.5F + Math.PI / 2F) * 0.025F);
                submitChildItem(itemModelResolver, shard, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
                matrixStackIn.popPose();
            }
            matrixStackIn.popPose();
        }
        if (stack.getItem() == AMItemRegistry.STINK_RAY) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack hand = new ItemStack(ItemStinkRay.isUsable(stack) ? AMItemRegistry.STINK_RAY_HAND : AMItemRegistry.STINK_RAY_EMPTY_HAND);
            ItemStack inventory = new ItemStack(ItemStinkRay.isUsable(stack) ? AMItemRegistry.STINK_RAY_INVENTORY : AMItemRegistry.STINK_RAY_EMPTY_INVENTORY);
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                submitChildItem(itemModelResolver, hand, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            } else {
                submitChildItem(itemModelResolver, inventory, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, seed);
            }
        }
        if (stack.getItem() == AMItemRegistry.TAB_ICON) {
            Entity fakeEntity = null;
            List<Pair<EntityType<?>, Float>> mobIcons = AMMobIcons.getMobIcons();
            int entityIndex = (tick / 40) % (mobIcons.size());
            float scale = 1.0F;
            int flags = 0;
            if (level != null) {
                if (ItemTabIcon.hasCustomEntityDisplay(stack)) {
                    flags = ItemTabIcon.getDisplayMobFlags(stack);
                    String index = ItemTabIcon.getCustomDisplayEntityString(stack);
                    EntityType<?> local = ItemTabIcon.getEntityType(stack);
                    scale = getScaleFor(local, mobIcons);
                    float componentScale = ItemTabIcon.getDisplayMobScale(stack);
                    if (componentScale > 0) {
                        scale = componentScale;
                    }
                    if (this.renderedEntites.get(index) == null && !blockedRenderEntities.contains(local)) {
                        try {
                            Entity entity = local.create(level, EntitySpawnReason.MOB_SUMMONED);
                            if (entity instanceof EntityBlobfish) {
                                ((EntityBlobfish) entity).setDepressurized(true);
                            }
                            this.renderedEntites.put(index, entity);
                            fakeEntity = entity;
                        } catch (Exception e) {
                            blockedRenderEntities.add(local);
                            AlexsMobs.LOGGER.error("Could not render item for entity: " + local);
                        }
                    } else {
                        fakeEntity = this.renderedEntites.get(index);
                    }
                } else {
                    EntityType<?> type = mobIcons.get(entityIndex).getFirst();
                    scale = mobIcons.get(entityIndex).getSecond();
                    if (type != null) {
                        if (this.renderedEntites.get(type.getDescriptionId()) == null && !blockedRenderEntities.contains(type)) {
                            try {
                                Entity entity = type.create(level, EntitySpawnReason.MOB_SUMMONED);
                                if (entity instanceof EntityBlobfish) {
                                    ((EntityBlobfish) entity).setDepressurized(true);
                                }
                                this.renderedEntites.put(type.getDescriptionId(), entity);
                                fakeEntity = entity;
                            } catch (Exception e) {
                                blockedRenderEntities.add(type);
                                AlexsMobs.LOGGER.error("Could not render item for entity: " + type);
                            }
                        } else {
                            fakeEntity = this.renderedEntites.get(type.getDescriptionId());
                        }
                    }
                }
            }

            if (fakeEntity instanceof EntityCockroach) {
                if (flags == 99) {
                    matrixStackIn.translate(0, 0.25F, 0);
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-80));
                    ((EntityCockroach) fakeEntity).setMaracas(true);
                } else {
                    ((EntityCockroach) fakeEntity).setMaracas(false);
                }
            }
            if (fakeEntity instanceof EntityElephant) {
                if (flags == 99) {
                    ((EntityElephant) fakeEntity).setTusked(true);
                    ((EntityElephant) fakeEntity).setColor(null);
                } else if (flags == 98) {
                    ((EntityElephant) fakeEntity).setTusked(false);
                    ((EntityElephant) fakeEntity).setColor(DyeColor.BROWN);
                } else {
                    ((EntityElephant) fakeEntity).setTusked(false);
                    ((EntityElephant) fakeEntity).setColor(null);
                }
            }
            if (fakeEntity instanceof EntityBaldEagle) {
                if (flags == 98) {
                    ((EntityBaldEagle) fakeEntity).setCap(true);
                } else {
                    ((EntityBaldEagle) fakeEntity).setCap(false);
                }
            }
            if (fakeEntity instanceof EntityVoidWorm) {
                matrixStackIn.translate(0, 0.5F, 0);
            }
            if (fakeEntity instanceof EntityMimicOctopus) {
                matrixStackIn.translate(0, 0.5F, 0);
            }
            if (fakeEntity instanceof EntityLaviathan) {
                RenderLaviathan.renderWithoutShaking = true;
                matrixStackIn.translate(0, 0.3F, 0);
            }
            if (fakeEntity instanceof EntityCosmaw) {
                matrixStackIn.translate(0, 0.2F, 0);
            }
            if (fakeEntity instanceof EntityGiantSquid) {
                matrixStackIn.translate(0, 0.5F, 0.3F);
            }
            if (fakeEntity instanceof EntityUnderminer) {
                RenderUnderminer.renderWithPickaxe = true;
            }
            if (fakeEntity instanceof EntityMurmur) {
                RenderMurmurBody.renderWithHead = true;
                matrixStackIn.translate(0, -0.2F, 0);
            }
            if (fakeEntity != null) {
                MouseHandler mouseHelper = Minecraft.getInstance().mouseHandler;
                double mouseX = (mouseHelper.xpos() * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth()) / (double) Minecraft.getInstance().getWindow().getScreenWidth();
                double mouseY = mouseHelper.ypos() * (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double) Minecraft.getInstance().getWindow().getScreenHeight();
                matrixStackIn.translate(0.5F, 0F, 0);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(180F));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180F));
                if (transformType != ItemDisplayContext.GUI) {
                    mouseX = 0;
                    mouseY = 0;
                }
                try {
                    drawEntityOnScreen(matrixStackIn, 0, 0, scale, true, 0, -45, 0, (float) mouseX, (float) mouseY, fakeEntity, bufferIn);
                } catch (Exception e) {
                    // same as original empty catch
                }
            }
            if (fakeEntity instanceof EntityLaviathan) {
                RenderLaviathan.renderWithoutShaking = false;
            }
            if (fakeEntity instanceof EntityUnderminer) {
                RenderUnderminer.renderWithPickaxe = false;
            }
            if (fakeEntity instanceof EntityMurmur) {
                RenderMurmurBody.renderWithHead = false;
            }
        }
    }

    private static void submitChildItem(ItemModelResolver resolver, ItemStack child, ItemDisplayContext transformType, int light, int overlay, PoseStack pose, SubmitNodeCollector collector, Level level, int seed) {
        ItemStackRenderState rs = new ItemStackRenderState();
        resolver.updateForTopItem(rs, child, transformType, level instanceof ClientLevel cl ? cl : null, null, seed);
        rs.submit(pose, collector, light, overlay, seed);
    }

    /**
     * {@link ItemModel} wired from model baking for all ISTER-style items.
     */
    public static final class SpecialItemModel implements ItemModel {
        public static final SpecialItemModel INSTANCE = new SpecialItemModel();

        private SpecialItemModel() {
        }

        @Override
        public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed) {
            state.appendModelIdentityElement(SpecialItemModel.INSTANCE);
            // BEWLR/ISTER drew every frame; without this, TrackingItemStackRenderState caches geometry and skips
            // per-frame submit — e.g. shattered dimensional carver shard motion (tick-based in submit) appears frozen.
            state.setAnimated();
            ItemStackRenderState.LayerRenderState layer = state.newLayer();
            if (stack.hasFoil()) {
                layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
                state.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
            }
            layer.setExtents(ItemStackRenderState.LayerRenderState.NO_EXTENTS_SUPPLIER);
            layer.setupSpecialModel(AMItemstackRenderer.INSTANCE, new AmSpecialItemPayload(stack, displayContext, owner));
            state.appendModelIdentityElement(stack);
        }
    }
}
