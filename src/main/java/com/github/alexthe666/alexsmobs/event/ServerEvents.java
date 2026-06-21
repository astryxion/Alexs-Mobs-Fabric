package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ILeftClick;
import com.github.alexthe666.alexsmobs.item.ItemGhostlyPickaxe;
import com.github.alexthe666.alexsmobs.network.MessageSwingArm;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;

import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.github.alexthe666.alexsmobs.world.BeachedCachalotWhaleSpawner;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ServerEvents {

    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    public static final UUID CARRO_UUID = UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290");
    private static final Identifier SAND_SPEED_ID = Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "roadrunner_sand_speed");
    private static final Identifier SNEAK_SPEED_ID = Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "frontier_cap_sneak_speed");
    private static final Random RAND = new Random();
    private static final String HAS_BOOK_TAG = "alexsmobs_has_book";
    private static final Queue<AfterDamageEntry> DEFERRED_AFTER_DAMAGE = new ConcurrentLinkedQueue<>();
    private static final Map<ServerLevel, BeachedCachalotWhaleSpawner> BEACHED_CACHALOT_WHALE_SPAWNER_MAP = new HashMap<>();
    public static final ObjectList<Triple<ServerPlayer, ServerLevel, BlockPos>> teleportPlayers = new ObjectArrayList<>();

    private static final class AfterDamageEntry {
        final LivingEntity entity;
        final DamageSource source;
        final float amount;

        AfterDamageEntry(LivingEntity entity, DamageSource source, float amount) {
            this.entity = entity;
            this.source = source;
            this.amount = amount;
        }
    }

    public record Triple<A, B, C>(A a, B b, C c) {}

    private static void enqueueAfterDamage(LivingEntity entity, DamageSource source, float amount) {
        DEFERRED_AFTER_DAMAGE.add(new AfterDamageEntry(entity, source, amount));
    }

    private static void runDeferredAfterDamage() {
        AfterDamageEntry entry;
        while ((entry = DEFERRED_AFTER_DAMAGE.poll()) != null) {
            float adjusted = onLivingDamageEvent(entry.entity, entry.source, entry.amount);
            if (adjusted <= 0 || !entry.entity.isAlive()) {
                continue;
            }
            if (!entry.entity.getUseItem().isEmpty() && entry.source.getEntity() instanceof LivingEntity attacker) {
                if (entry.entity.getUseItem().is(AMItemRegistry.SHIELD_OF_THE_DEEP)) {
                    boolean flag = false;
                    if (attacker.distanceTo(entry.entity) <= 4 && !attacker.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.EXSANGUINATION))) {
                        attacker.addEffect(new MobEffectInstance(net.minecraft.core.Holder.direct(AMEffectRegistry.EXSANGUINATION), 60, 2));
                        flag = true;
                    }
                    if (AMEntityRegistry.isInWaterOrBubble(entry.entity)) {
                        entry.entity.setAirSupply(Math.min(entry.entity.getMaxAirSupply(), entry.entity.getAirSupply() + 150));
                        flag = true;
                    }
                    if (flag) {
                        entry.entity.getUseItem().hurtAndBreak(1, entry.entity, entry.entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    }
                }
            }
        }
    }

    private static void onLevelTick(ServerLevel level) {
        BEACHED_CACHALOT_WHALE_SPAWNER_MAP.computeIfAbsent(level, k -> new BeachedCachalotWhaleSpawner(level));
        BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(level).tick();
        AMWorldData data = AMWorldData.get(level);
        if (data != null) {
            data.tickPupfish();
        }
    }

    private static void resetArrowPierceLevel(AbstractArrow arrow) {
        try {
            java.lang.reflect.Method method = AbstractArrow.class.getDeclaredMethod("setPierceLevel", byte.class);
            method.setAccessible(true);
            method.invoke(arrow, (byte) 0);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    /** Fabric: register server/world/entity events here (replaces Forge EVENT_BUS). */
    public static void register() {
        AlexsMobs.LOGGER.info("Registering Alex's Mobs server events");

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                onLevelTick(level);
            }
            runDeferredAfterDamage();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerLoggedIn(handler.getPlayer()));

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getType() == EntityTypes.SQUID && !entity.level().isClientSide() && source.is(DamageTypeTags.IS_LIGHTNING)) {
                onStruckByLightning(entity);
                return false;
            }
            if (entity instanceof EntityEmu emu && source.getDirectEntity() instanceof Projectile projectile && !entity.level().isClientSide()) {
                if (projectile instanceof AbstractArrow arrow) {
                    resetArrowPierceLevel(arrow);
                }
                if ((emu.getAnimation() == EntityEmu.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EntityEmu.ANIMATION_DODGE_LEFT) && emu.getAnimationTick() < 7) {
                    return false;
                }
                if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
                    Vec3 arrowPos = projectile.position();
                    Vec3 rightVector = emu.getLookAngle().yRot(0.5F * Mth.PI).add(emu.position());
                    Vec3 leftVector = emu.getLookAngle().yRot(-0.5F * Mth.PI).add(emu.position());
                    boolean left = arrowPos.distanceTo(rightVector) >= arrowPos.distanceTo(leftVector);
                    Vec3 vector3d2 = projectile.getDeltaMovement().yRot((float) ((left ? -0.5F : 0.5F) * Math.PI)).normalize();
                    emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
                    emu.needsSync = true;
                    if (!emu.horizontalCollision) {
                        emu.move(MoverType.SELF, new Vec3(vector3d2.x() * 0.25F, 0.1F, vector3d2.z() * 0.25F));
                    }
                    if (projectile.getOwner() instanceof ServerPlayer serverPlayer) {
                        AMAdvancementTriggerRegistry.EMU_DODGE.trigger(serverPlayer);
                    }
                    emu.setDeltaMovement(emu.getDeltaMovement().add(vector3d2.x() * 0.5F, 0.32F, vector3d2.z() * 0.5F));
                    return false;
                }
            }
            if (entity instanceof Player player && source.getEntity() instanceof EntityMimicOctopus octopus && octopus.isOwnedBy(player)) {
                return false;
            }
            if (!entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty() && entity.getItemBySlot(EquipmentSlot.LEGS).is(AMItemRegistry.EMU_LEGGINGS)) {
                if (source.is(DamageTypeTags.IS_PROJECTILE) && entity.getRandom().nextFloat() < AMConfig.emuPantsDodgeChance) {
                    return false;
                }
            }
            if (amount > 0) {
                enqueueAfterDamage(entity, source, amount);
            }
            return true;
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.DEBILITATING_STING))
                    && entity.getEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.DEBILITATING_STING)) != null
                    && entity.getEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.DEBILITATING_STING)).getAmplifier() > 0
                    && entity instanceof Mob mob) {
                mob.setPersistenceRequired();
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (VineLassoUtil.hasLassoData(entity)) {
                VineLassoUtil.lassoTo(null, entity);
                entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(AMItemRegistry.VINE_LASSO)));
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof WanderingTrader trader && AMConfig.elephantTraderSpawnChance > 0) {
                Biome biome = serverWorld.getBiome(entity.blockPosition()).value();
                if (RAND.nextFloat() <= AMConfig.elephantTraderSpawnChance && (!AMConfig.limitElephantTraderBiomes || biome.getBaseTemperature() >= 1.0F)) {
                    BlockPos traderPos = trader.blockPosition();
                    ChunkPos chunkPos = new ChunkPos(traderPos.getX() >> 4, traderPos.getZ() >> 4);
                    if (serverWorld.getChunkSource().getChunkNow(chunkPos.x(), chunkPos.z()) != null) {
                        EntityElephant elephant = AMEntityRegistry.ELEPHANT.create(serverWorld, EntitySpawnReason.TRIGGERED);
                        if (elephant != null) {
                            elephant.snapTo(trader.getX(), trader.getY(), trader.getZ(), trader.getYRot(), trader.getXRot());
                            if (elephant.canSpawnWithTraderHere()) {
                                elephant.setTrader(true);
                                elephant.setChested(true);
                                serverWorld.addFreshEntity(elephant);
                                trader.startRiding(elephant);
                            }
                            elephant.addElephantLoot(null, RAND.nextInt());
                        }
                    }
                }
            }
            onEntityJoinLevel(entity);
            onEntityJoinWanderingTrader(serverWorld, entity);
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            onPlayerAttackEntityEvent(player, entity);
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> onUseItemOnBlock(player, hitResult.getBlockPos(), player.getItemInHand(hand)));

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
                onInteractWithEntity(player, entity, world, player.getItemInHand(hand)));

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            onUseItem(world, player, hand, stack);
            if (RainbowUtil.getRainbowType(player) > 0 && stack.is(Items.SPONGE)) {
                onUseItemAir(player, hand);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> !onHarvestCheck(player));
    }

    public static void onLivingEntityTick(LivingEntity entity) {
        onLivingTick(entity);
    }

    @SuppressWarnings("unchecked")
    private static void addGoalReflective(Mob mob, boolean targetGoals, int priority, net.minecraft.world.entity.ai.goal.Goal goal) {
        try {
            java.lang.reflect.Field field = Mob.class.getDeclaredField(targetGoals ? "targetSelector" : "goalSelector");
            field.setAccessible(true);
            Object selector = field.get(mob);
            java.lang.reflect.Method addGoal = selector.getClass().getMethod("addGoal", int.class, net.minecraft.world.entity.ai.goal.Goal.class);
            addGoal.invoke(selector, priority, goal);
        } catch (ReflectiveOperationException e) {
            AlexsMobs.LOGGER.warn("Failed to add reflective mob goal {} to {}", goal.getClass().getSimpleName(), mob.getType(), e);
        }
    }

    protected static BlockHitResult rayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        float x = player.getXRot();
        float y = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        float f0 = -y * Mth.DEG_TO_RAD - Mth.PI;
        float f1 = -x * Mth.DEG_TO_RAD;
        float f2 = Mth.cos(f0);
        float f3 = Mth.sin(f0);
        float f4 = -Mth.cos(f1);
        float f5 = Mth.sin(f1);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.blockInteractionRange();
        Vec3 vector3d1 = vector3d.add(f6 * d0, f5 * d0, f7 * d0);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    /**
     * Check if an entity should not scare pufferfish (Alex's Mobs fish and other aquatic creatures)
     */
    private static boolean isNotScaryForPufferfish(LivingEntity entity) {
        return entity instanceof EntityLobster ||
               entity instanceof EntityBlobfish ||
               entity instanceof EntityTerrapin ||
               entity instanceof EntityCombJelly ||
               entity instanceof EntityCosmicCod ||
               entity instanceof EntityCatfish ||
               entity instanceof EntityFlyingFish ||
               entity instanceof EntityMudskipper ||
               entity instanceof EntityTriops ||
               entity instanceof EntityDevilsHolePupfish ||
               entity instanceof AbstractFish ||  // Vanilla fish
               entity instanceof AbstractSchoolingFish ||  // Vanilla schooling fish
               entity instanceof Squid ||  // Vanilla squid
               entity instanceof Dolphin;  // Vanilla dolphin
    }

    private static net.minecraft.network.syncher.EntityDataAccessor<Integer> PUFF_STATE_ACCESSOR = null;
    private static boolean PUFF_STATE_INIT_ATTEMPTED = false;
    
    /**
     * Set pufferfish puff state using reflection
     */
    @SuppressWarnings("unchecked")
    private static void setPufferfishState(Pufferfish pufferfish, int state) {
        if (!PUFF_STATE_INIT_ATTEMPTED) {
            PUFF_STATE_INIT_ATTEMPTED = true;
            try {
                // Find the PUFF_STATE field by scanning all static fields
                for (var field : Pufferfish.class.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && 
                        net.minecraft.network.syncher.EntityDataAccessor.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        var accessor = (net.minecraft.network.syncher.EntityDataAccessor<?>) field.get(null);
                        // Test if this is the puff state accessor by checking current value type
                        try {
                            Object value = pufferfish.getEntityData().get(accessor);
                            if (value instanceof Integer && field.getName().toLowerCase().contains("puff")) {
                                PUFF_STATE_ACCESSOR = (net.minecraft.network.syncher.EntityDataAccessor<Integer>) accessor;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                }
                // If not found by name, try to find by matching the getter value
                if (PUFF_STATE_ACCESSOR == null) {
                    int currentPuffState = pufferfish.getPuffState();
                    for (var field : Pufferfish.class.getDeclaredFields()) {
                        if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && 
                            net.minecraft.network.syncher.EntityDataAccessor.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            var accessor = (net.minecraft.network.syncher.EntityDataAccessor<?>) field.get(null);
                            try {
                                Object value = pufferfish.getEntityData().get(accessor);
                                if (value instanceof Integer intVal && intVal == currentPuffState) {
                                    PUFF_STATE_ACCESSOR = (net.minecraft.network.syncher.EntityDataAccessor<Integer>) accessor;
                                    break;
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                AlexsMobs.LOGGER.warn("Failed to find PUFF_STATE field: " + e.getMessage());
            }
        }
        
        if (PUFF_STATE_ACCESSOR != null) {
            pufferfish.getEntityData().set(PUFF_STATE_ACCESSOR, state);
        }
    }

    private static java.lang.reflect.Field INFLATE_COUNTER_FIELD = null;
    private static java.lang.reflect.Field DEFLATE_TIMER_FIELD = null;
    private static boolean INFLATE_FIELDS_INIT_ATTEMPTED = false;
    
    /**
     * Reset pufferfish inflate counter to prevent re-inflation
     */
    private static void resetPufferfishInflateCounter(Pufferfish pufferfish) {
        if (!INFLATE_FIELDS_INIT_ATTEMPTED) {
            INFLATE_FIELDS_INIT_ATTEMPTED = true;
            try {
                // Find inflateCounter and deflateTimer fields
                for (var field : Pufferfish.class.getDeclaredFields()) {
                    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                        String name = field.getName().toLowerCase();
                        if (name.contains("inflate") || name.contains("counter")) {
                            field.setAccessible(true);
                            INFLATE_COUNTER_FIELD = field;
                        } else if (name.contains("deflate") || name.contains("timer")) {
                            field.setAccessible(true);
                            DEFLATE_TIMER_FIELD = field;
                        }
                    }
                }
                // If not found by name, get all int fields
                if (INFLATE_COUNTER_FIELD == null) {
                    for (var field : Pufferfish.class.getDeclaredFields()) {
                        if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                            field.setAccessible(true);
                            // The first non-static int field is likely inflateCounter
                            if (INFLATE_COUNTER_FIELD == null) {
                                INFLATE_COUNTER_FIELD = field;
                            } else if (DEFLATE_TIMER_FIELD == null) {
                                DEFLATE_TIMER_FIELD = field;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                AlexsMobs.LOGGER.warn("Failed to find inflate counter fields: " + e.getMessage());
            }
        }
        
        try {
            if (INFLATE_COUNTER_FIELD != null) {
                INFLATE_COUNTER_FIELD.setInt(pufferfish, 0);
            }
            if (DEFLATE_TIMER_FIELD != null) {
                DEFLATE_TIMER_FIELD.setInt(pufferfish, 0);
            }
        } catch (Exception ignored) {}
    }

    private static BlockPos getDownPos(BlockPos entered, LevelAccessor world) {
        int i = 0;
        while (world.isEmptyBlock(entered) && i < 3) {
            entered = entered.below();
            i++;
        }
        return entered;
    }

    private static void giveItemToPlayer(Player player, ItemStack stack) {
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }

        public static void onPlayerLoggedIn(Player player) {
        if (AMConfig.giveBookOnStartup) {
            if (!player.entityTags().contains(HAS_BOOK_TAG)) {
                giveItemToPlayer(player, new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY));
                boolean isAlex = Objects.equals(player.getUUID(), ALEX_UUID);
                if (isAlex || Objects.equals(player.getUUID(), CARRO_UUID)) {
                    giveItemToPlayer(player, new ItemStack(AMItemRegistry.BEAR_DUST));
                }
                if (isAlex) {
                    giveItemToPlayer(player, new ItemStack(AMItemRegistry.NOVELTY_HAT));
                }
                player.addTag(HAS_BOOK_TAG);
            }
        }
    }

    /**
     * Client-only: empty left-click with an {@link ILeftClick} item must notify the server (vanilla never does).
     * Matches {@code ServerEvents.java.original} / 1.21.1 behavior for tendon whip and falconry glove.
     */
        public static void onPlayerLeftClick(Player player) {
        boolean flag = false;
        ItemStack leftItem = player.getOffhandItem();
        ItemStack rightItem = player.getMainHandItem();
        if (leftItem.getItem() instanceof ILeftClick iLeftClick) {
            iLeftClick.onLeftClick(leftItem, player);
            flag = true;
        }
        if (rightItem.getItem() instanceof ILeftClick iLeftClick) {
            iLeftClick.onLeftClick(rightItem, player);
            flag = true;
        }
        if (flag && player.level().isClientSide()) {
            AlexsMobs.sendMSGToServer(new MessageSwingArm());
        }
    }

        public static void onPlayerAttackEntityEvent(Player player, Entity target) {
        if (target instanceof LivingEntity living) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.MOOSE_HEADGEAR) {
                living.knockback(1F, Mth.sin(player.getYRot() * Mth.DEG_TO_RAD),
                        -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD), player.damageSources().playerAttack(player), 0.0F);
            }
            if (player.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.TIGERS_BLESSING))
                    && !target.isAlliedTo(player) && !(target instanceof EntityTiger)) {
                AABB bb = new AABB(player.getX() - 32, player.getY() - 32, player.getZ() - 32,
                        player.getX() + 32, player.getY() + 32, player.getZ() + 32);
                var tigers = player.level().getEntitiesOfClass(EntityTiger.class, bb, EntitySelector.ENTITY_STILL_ALIVE);
                for (EntityTiger tiger : tigers) {
                    if (!tiger.isBaby()) {
                        tiger.setTarget(living);
                    }
                }
            }
        }
    }

        public static boolean onHarvestCheck(Player player) {
        return player != null && player.isHolding(AMItemRegistry.GHOSTLY_PICKAXE)
                && ItemGhostlyPickaxe.shouldStoreInGhost(player, player.getMainHandItem());
    }

        public static void onStruckByLightning(Entity entity) {
        if (entity.getType() == EntityTypes.SQUID && !entity.level().isClientSide()) {
            ServerLevel level = (ServerLevel) entity.level();
            EntityGiantSquid squid = AMEntityRegistry.GIANT_SQUID.create(level, EntitySpawnReason.CONVERSION);
            squid.snapTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
            squid.finalizeSpawn(level, level.getCurrentDifficultyAt(squid.blockPosition()), EntitySpawnReason.CONVERSION, null);
            if (entity.hasCustomName()) {
                squid.setCustomName(entity.getCustomName());
                squid.setCustomNameVisible(entity.isCustomNameVisible());
            }
            squid.setBlue(true);
            squid.setPersistenceRequired();
            level.addFreshEntityWithPassengers(squid);
            entity.discard();
        }
    }

        public static boolean onProjectileHit(Entity projectileEntity, HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult
                && entityHitResult.getEntity() instanceof EntityEmu emu && !projectileEntity.level().isClientSide()) {
            if ((emu.getAnimation() == EntityEmu.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EntityEmu.ANIMATION_DODGE_LEFT)
                    && emu.getAnimationTick() < 7) {
                return true;
            }
            if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
                boolean left;
                Vec3 arrowPos = projectileEntity.position();
                Vec3 rightVector = emu.getLookAngle().yRot(0.5F * Mth.PI).add(emu.position());
                Vec3 leftVector = emu.getLookAngle().yRot(-0.5F * Mth.PI).add(emu.position());
                if (arrowPos.distanceTo(rightVector) < arrowPos.distanceTo(leftVector)) {
                    left = false;
                } else if (arrowPos.distanceTo(rightVector) > arrowPos.distanceTo(leftVector)) {
                    left = true;
                } else {
                    left = emu.getRandom().nextBoolean();
                }
                Vec3 vector3d2 = projectileEntity.getDeltaMovement().yRot((float) ((left ? -0.5F : 0.5F) * Math.PI)).normalize();
                emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
                emu.needsSync = true;
                if (!emu.horizontalCollision) {
                    emu.move(MoverType.SELF, new Vec3(vector3d2.x() * 0.25F, 0.1F, vector3d2.z() * 0.25F));
                }
                if (projectileEntity instanceof Projectile projectile) {
                    if (projectile.getOwner() instanceof ServerPlayer serverPlayer) {
                        AlexsMobs.LOGGER.info("Emu dodged! Triggering EMU_DODGE advancement for player: {}", serverPlayer.getName().getString());
                        AMAdvancementTriggerRegistry.EMU_DODGE.trigger(serverPlayer);
                    }
                }
                emu.setDeltaMovement(emu.getDeltaMovement().add(vector3d2.x() * 0.5F, 0.32F, vector3d2.z() * 0.5F));
                return true;
            }
        }
        return false;
    }

        public static void onEntityJoinLevel(Entity entity) {
        try {
            if (AMConfig.spidersAttackFlies && entity instanceof Spider spider) {
                addGoalReflective(spider, true, 4, new NearestAttackableTargetGoal<>(spider, EntityFly.class, 1, true, false, null));
            } else if (AMConfig.wolvesAttackMoose && entity instanceof Wolf wolf) {
                addGoalReflective(wolf, true, 6, new NonTameRandomTargetGoal<>(wolf, EntityMoose.class, false, null));
            } else if (AMConfig.polarBearsAttackSeals && entity instanceof PolarBear bear) {
                addGoalReflective(bear, true, 6, new NearestAttackableTargetGoal<>(bear, EntitySeal.class, 15, true, true, null));
            } else if (entity instanceof Creeper creeper) {
                addGoalReflective(creeper, true, 3, new AvoidEntityGoal<>(creeper, EntitySnowLeopard.class, 6.0F, 1.0D, 1.2D));
                addGoalReflective(creeper, true, 3, new AvoidEntityGoal<>(creeper, EntityTiger.class, 6.0F, 1.0D, 1.2D));
            } else if (AMConfig.catsAndFoxesAttackJerboas && (entity instanceof Fox || entity instanceof Cat || entity instanceof Ocelot)) {
                Mob mb = (Mob) entity;
                addGoalReflective(mb, true, 6, new NearestAttackableTargetGoal<>(mb, EntityJerboa.class, 45, true, true, null));
            } else if (AMConfig.bunfungusTransformation && entity instanceof Rabbit rabbit) {
                addGoalReflective(rabbit, false, 3, new TemptGoal(rabbit, 1.0D, Ingredient.of(AMItemRegistry.MUNGAL_SPORES), false));
            } else if (AMConfig.dolphinsAttackFlyingFish && entity instanceof Dolphin dolphin) {
                addGoalReflective(dolphin, true, 2, new NearestAttackableTargetGoal<>(dolphin, EntityFlyingFish.class, 70, true, true, null));
            }
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

        public static void onEntityDrops(LivingEntity entity, List<ItemEntity> drops) {
        if (VineLassoUtil.hasLassoData(entity)) {
            VineLassoUtil.lassoTo(null, entity);
            drops.add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(AMItemRegistry.VINE_LASSO)));
        }
    }

        public static void onItemUseLast(LivingEntity entity, ItemStack usedItem) {
        if (usedItem.getItem() == Items.CHORUS_FRUIT && RAND.nextInt(3) == 0
                && entity.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.ENDER_FLU))) {
            entity.removeEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.ENDER_FLU));
        }
    }


        public static void onLivingTick(LivingEntity entity) {
        if (!entity.level().isClientSide() && entity instanceof Mob mob && mob.getTarget() != null) {
            LivingEntity target = mob.getTarget();
            if (shouldCancelTargeting(mob, target)) {
                mob.setTarget(null);
            }
        }
        if (!entity.level().isClientSide()
                && entity instanceof net.minecraft.world.entity.player.Player player
                && player.isFallFlying()
                && player.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.TARANTULA_HAWK_ELYTRA)) {
            int ticks = player.getFallFlyingTicks();
            if ((ticks + 1) % 20 == 0) {
                player.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(1, entity, EquipmentSlot.CHEST);
            }
        }
        // Make pufferfish not puff up around Alex's Mobs fish
        if (entity instanceof Pufferfish pufferfish && !entity.level().isClientSide()) {
            // Check if only Alex's Mobs fish are nearby (no scary entities)
            var nearbyEntities = pufferfish.level().getEntitiesOfClass(LivingEntity.class, 
                pufferfish.getBoundingBox().inflate(2.0D), 
                e -> e != pufferfish && !(e instanceof Pufferfish) && !e.isSpectator());
            
            boolean hasScaryEntity = false;
            boolean hasFriendlyFish = false;
            for (LivingEntity nearby : nearbyEntities) {
                if (isNotScaryForPufferfish(nearby)) {
                    hasFriendlyFish = true;
                } else {
                    hasScaryEntity = true;
                    break;
                }
            }
            // If only friendly fish nearby (no scary entities), keep pufferfish deflated
            if (hasFriendlyFish && !hasScaryEntity) {
                setPufferfishState(pufferfish, 0);
                resetPufferfishInflateCounter(pufferfish);
            }
        }
        
        if (entity instanceof Player player) {
            if (entity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                var attributes = entity.getAttribute(Attributes.MOVEMENT_SPEED);
                if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS
                        || attributes.hasModifier(SAND_SPEED_ID)) {
                    boolean sand = player.level().getBlockState(getDownPos(player.blockPosition(), player.level())).is(BlockTags.SAND);
                    if (sand && !attributes.hasModifier(SAND_SPEED_ID)) {
                        attributes.addPermanentModifier(new AttributeModifier(SAND_SPEED_ID, 0.1F, AttributeModifier.Operation.ADD_VALUE));
                    }
                    if (player.tickCount % 25 == 0
                            && (player.getItemBySlot(EquipmentSlot.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS || !sand)
                            && attributes.hasModifier(SAND_SPEED_ID)) {
                        attributes.removeModifier(SAND_SPEED_ID);
                    }
                }
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.FRONTIER_CAP
                        || attributes.hasModifier(SNEAK_SPEED_ID)) {
                    boolean shift = player.isShiftKeyDown();
                    if (shift && !attributes.hasModifier(SNEAK_SPEED_ID)) {
                        attributes.addPermanentModifier(new AttributeModifier(SNEAK_SPEED_ID, 0.1F, AttributeModifier.Operation.ADD_VALUE));
                    }
                    if ((!shift || player.getItemBySlot(EquipmentSlot.HEAD).getItem() != AMItemRegistry.FRONTIER_CAP)
                            && attributes.hasModifier(SNEAK_SPEED_ID)) {
                        attributes.removeModifier(SNEAK_SPEED_ID);
                    }
                }
            }
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL) {
                if (!player.isEyeInFluid(FluidTags.WATER)) {
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 310, 0, false, false, true));
                }
            }
        }
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.isEmpty()) {
            CompoundTag tag = boots.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
            if (tag.getBooleanOr("BisonFur", false)) {
                BlockPos posBelow = new BlockPos((int) entity.getX(), (int) (entity.getBoundingBox().minY - 0.1F), (int) entity.getZ());
                if (entity.level().getBlockState(posBelow).is(Blocks.POWDER_SNOW)) {
                    entity.setOnGround(true);
                    entity.setTicksFrozen(0);
                    entity.setPos(entity.getX(), Math.max(entity.getY(), posBelow.getY() + 1F), entity.getZ());
                }
                if (entity.isInPowderSnow) {
                    entity.setOnGround(true);
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1F, 0));
                }
            }
        }
        if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS) {
            if (entity.horizontalCollision && !entity.isInWater()) {
                entity.fallDistance = 0.0F;
                Vec3 motion = entity.getDeltaMovement();
                double d2 = 0.1D;
                if (entity.isShiftKeyDown() || entity.isSuppressingSlidingDownLadder()) {
                    d2 = 0.0D;
                }
                motion = new Vec3(Mth.clamp(motion.x, -0.15F, 0.15F), d2, Mth.clamp(motion.z, -0.15F, 0.15F));
                entity.setDeltaMovement(motion);
            }
        }

        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SOMBRERO
                && !entity.level().isClientSide() && AlexsMobs.isAprilFools() && AMEntityRegistry.isInWaterOrBubble(entity)) {
            RandomSource random = entity.getRandom();
            if (random.nextInt(245) == 0 && !EntitySeaBear.isMobSafe(entity)) {
                int dist = 32;
                var nearbySeabears = entity.level().getEntitiesOfClass(EntitySeaBear.class, entity.getBoundingBox().inflate(dist, dist, dist));
                if (nearbySeabears.isEmpty() && entity.level() instanceof ServerLevel serverLevel) {
                    EntitySeaBear bear = AMEntityRegistry.SEA_BEAR.create(serverLevel, EntitySpawnReason.TRIGGERED);
                    if (bear != null) {
                        BlockPos at = entity.blockPosition();
                        BlockPos farOff = null;
                        for (int i = 0; i < 15; i++) {
                            int f1 = (int) Math.signum(random.nextInt() - 0.5F);
                            int f2 = (int) Math.signum(random.nextInt() - 0.5F);
                            BlockPos pos1 = at.offset(f1 * (10 + random.nextInt(dist - 10)), random.nextInt(1), f2 * (10 + random.nextInt(dist - 10)));
                            if (entity.level().isWaterAt(pos1)) {
                                farOff = pos1;
                            }
                        }
                        if (farOff != null) {
                            bear.setPos(farOff.getX() + 0.5F, farOff.getY() + 0.5F, farOff.getZ() + 0.5F);
                            bear.setYRot(random.nextFloat() * 360F);
                            bear.setTarget(entity);
                            entity.level().addFreshEntity(bear);
                        }
                    }
                } else {
                    for (EntitySeaBear seaBear : nearbySeabears) {
                        seaBear.setTarget(entity);
                    }
                }
            }
        }
        if (VineLassoUtil.hasLassoData(entity)) {
            VineLassoUtil.tickLasso(entity);
        }
        if (RockyChestplateUtil.isWearing(entity)) {
            RockyChestplateUtil.tickRockyRolling(entity);
        }
        if (FlyingFishBootsUtil.isWearing(entity)) {
            FlyingFishBootsUtil.tickFlyingFishBoots(entity);
        }
    }

        public static boolean shouldCancelTargeting(Mob mob, LivingEntity newTarget) {
        if (newTarget != null) {
            // Check for arthropod type using vanilla tag
            if (mob.getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.ARTHROPOD)) {
                if (newTarget.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.BUG_PHEROMONES))
                        && mob.getLastHurtByMob() != newTarget) {
                    return true;
                }
            }
            // Check for undead type using vanilla tag
            if (mob.getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.UNDEAD) && !mob.getType().builtInRegistryHolder().is(AMTagRegistry.IGNORES_KIMONO)) {
                if (newTarget.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO)
                        && mob.getLastHurtByMob() != newTarget) {
                    return true;
                }
            }
        }
        return false;
    }


        public static float onLivingDamageEvent(LivingEntity target, DamageSource source, float incomingDamage) {
        float damage = incomingDamage;
        if (source.getEntity() instanceof LivingEntity attacker) {
            if (damage > 0 && attacker.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.SOULSTEAL))
                    && attacker.getEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.SOULSTEAL)) != null) {
                int level = attacker.getEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.SOULSTEAL)).getAmplifier() + 1;
                if (attacker.getHealth() < attacker.getMaxHealth()
                        && ThreadLocalRandom.current().nextFloat() < (0.25F + (level * 0.25F))) {
                    attacker.heal(Math.min(damage / 2F * level, 2 + 2 * level));
                }
            }
            if (target instanceof Player player) {
                if (attacker instanceof EntityMimicOctopus octopus && octopus.isOwnedBy(player)) {
                    return 0;
                }
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL) {
                    if (attacker.distanceTo(player) < attacker.getBbWidth() + player.getBbWidth() + 0.5F) {
                        attacker.hurt(attacker.damageSources().thorns(player), 1F);
                        attacker.knockback(0.5F, Mth.sin((attacker.getYRot() + 180) * Mth.DEG_TO_RAD),
                                -Mth.cos((attacker.getYRot() + 180) * Mth.DEG_TO_RAD), player.damageSources().thorns(player), 0.0F);
                    }
                }
            }
        }
        if (!target.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                && target.getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.EMU_LEGGINGS) {
            if (source.is(DamageTypeTags.IS_PROJECTILE) && target.getRandom().nextFloat() < AMConfig.emuPantsDodgeChance) {
                damage = 0;
            }
        }
        return damage;
    }

        public static void onUseItem(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        if (itemStack.getItem() == Items.WHEAT && player.getVehicle() instanceof EntityElephant elephant) {
            if (elephant.triggerCharge(itemStack)) {
                player.swing(hand);
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
            }
        }
        if (itemStack.getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled) {
            HitResult raytraceresult = rayTrace(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
                if (level.mayInteract(player, blockpos)) {
                    if (level.getFluidState(blockpos).is(FluidTags.LAVA)) {
                        player.gameEvent(GameEvent.ITEM_INTERACT_START);
                        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        player.igniteForSeconds(6);
                        if (!player.addItem(new ItemStack(AMItemRegistry.LAVA_BOTTLE))) {
                            player.spawnAtLocation((ServerLevel) level, new ItemStack(AMItemRegistry.LAVA_BOTTLE));
                        }
                        player.swing(hand);
                        if (!player.isCreative()) {
                            itemStack.shrink(1);
                        }
                    }
                }
            }
        }
    }


        public static InteractionResult onInteractWithEntity(Player player, Entity target, Level level, ItemStack itemStack) {
        if (target instanceof LivingEntity living) {
            if (!player.isShiftKeyDown() && VineLassoUtil.hasLassoData(living)) {
                if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
                    target.spawnAtLocation(serverLevel, new ItemStack(AMItemRegistry.VINE_LASSO));
                }
                VineLassoUtil.lassoTo(null, living);
                return InteractionResult.SUCCESS;
            }
            if (!(target instanceof Player) && !(target instanceof EntityEndergrade)
                    && living.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.ENDER_FLU))) {
                if (itemStack.getItem() == Items.CHORUS_FRUIT) {
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    target.gameEvent(GameEvent.EAT);
                    target.playSound(SoundEvents.GENERIC_EAT.value(), 1.0F, 0.5F + player.getRandom().nextFloat());
                    if (player.getRandom().nextFloat() < 0.4F) {
                        living.removeEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.ENDER_FLU));
                        Items.CHORUS_FRUIT.finishUsingItem(itemStack.copy(), level, living);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            if (RainbowUtil.getRainbowType(living) > 0 && itemStack.getItem() == Items.SPONGE) {
                RainbowUtil.setRainbowType(living, 0);
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
                if (!player.addItem(wetSponge)) {
                    player.drop(wetSponge, true);
                }
                return InteractionResult.SUCCESS;
            }
            if (living instanceof Rabbit rabbit && itemStack.getItem() == AMItemRegistry.MUNGAL_SPORES
                    && AMConfig.bunfungusTransformation) {
                var random = ThreadLocalRandom.current();
                if (!player.level().isClientSide() && random.nextFloat() < 0.15F) {
                    EntityBunfungus bunfungus = rabbit.convertTo(
                            AMEntityRegistry.BUNFUNGUS,
                            ConversionParams.single(rabbit, true, false),
                            EntitySpawnReason.CONVERSION,
                            bun -> {});
                    if (bunfungus != null) {
                        player.level().addFreshEntity(bunfungus);
                        bunfungus.setTransformsIn(EntityBunfungus.MAX_TRANSFORM_TIME);
                    }
                } else {
                    for (int i = 0; i < 2 + random.nextInt(2); i++) {
                        double d0 = random.nextGaussian() * 0.02D;
                        double d1 = 0.05F + random.nextGaussian() * 0.02D;
                        double d2 = random.nextGaussian() * 0.02D;
                        target.level().addParticle(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION,
                                target.getRandomX(0.7F), target.getY(0.6F), target.getRandomZ(0.7F), d0, d1, d2);
                    }
                }
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }


        public static void onUseItemAir(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            stack = player.getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if (RainbowUtil.getRainbowType(player) > 0 && stack.is(Items.SPONGE)) {
            player.swing(InteractionHand.MAIN_HAND);
            RainbowUtil.setRainbowType(player, 0);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
            if (!player.addItem(wetSponge)) {
                player.drop(wetSponge, true);
            }
        }
    }

        public static InteractionResult onUseItemOnBlock(Player player, BlockPos pos, ItemStack itemStack) {
        if (AlexsMobs.isAprilFools() && itemStack.is(Items.STICK)
                && !player.getCooldowns().isOnCooldown(new ItemStack(Items.STICK))) {
            BlockState state = player.level().getBlockState(pos);
            boolean flag = false;
            if (state.is(Blocks.SAND)) {
                flag = true;
                player.level().setBlockAndUpdate(pos, AMBlockRegistry.SAND_CIRCLE.defaultBlockState());
            } else if (state.is(Blocks.RED_SAND)) {
                flag = true;
                player.level().setBlockAndUpdate(pos, AMBlockRegistry.RED_SAND_CIRCLE.defaultBlockState());
            }
            if (flag) {
                player.gameEvent(GameEvent.BLOCK_PLACE);
                player.playSound(SoundEvents.SAND_BREAK, 1, 1);
                player.getCooldowns().addCooldown(new ItemStack(Items.STICK), 30);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Villager trades are registered via data pack ({@code data/alexsmobs/villager_trade}). This filters offers when
     * {@link AMConfig} disables wandering trader integration or specific mob spawns (matching old event behavior).
     */
        public static void onEntityJoinWanderingTrader(Level level, Entity entity) {
        if (level.isClientSide() || !(entity instanceof WanderingTrader trader)) {
            return;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.getServer().execute(() -> {
            if (!trader.isAlive()) {
                return;
            }
            if (!AMConfig.wanderingTraderOffers) {
                trader.getOffers().removeIf(ServerEvents::isAlexsmobsWanderingTrade);
                return;
            }
            if (AMConfig.cockroachSpawnWeight <= 0) {
                trader.getOffers().removeIf(offer -> offer.getResult().is(AMItemRegistry.COCKROACH_OOTHECA));
            }
            if (AMConfig.blobfishSpawnWeight <= 0) {
                trader.getOffers().removeIf(offer -> offer.getResult().is(AMItemRegistry.BLOBFISH_BUCKET));
            }
            if (AMConfig.crocodileSpawnWeight <= 0) {
                trader.getOffers().removeIf(offer -> offer.getResult().is(AMBlockRegistry.CROCODILE_EGG.asItem()));
            }
        });
    }

    private static boolean isAlexsmobsWanderingTrade(net.minecraft.world.item.trading.MerchantOffer offer) {
        return offer.getResult().is(AMItemRegistry.ANIMAL_DICTIONARY)
                || offer.getResult().is(AMItemRegistry.ACACIA_BLOSSOM)
                || offer.getResult().is(AMItemRegistry.COCKROACH_OOTHECA)
                || offer.getResult().is(AMItemRegistry.BLOBFISH_BUCKET)
                || offer.getResult().is(AMBlockRegistry.CROCODILE_EGG.asItem())
                || offer.getResult().is(AMItemRegistry.BEAR_FUR)
                || offer.getResult().is(AMItemRegistry.CROCODILE_SCUTE)
                || offer.getResult().is(AMItemRegistry.ROADRUNNER_FEATHER)
                || offer.getResult().is(AMItemRegistry.MOSQUITO_LARVA)
                || offer.getResult().is(AMItemRegistry.SOMBRERO)
                || offer.getResult().is(AMBlockRegistry.BANANA_PEEL.asItem())
                || offer.getResult().is(AMItemRegistry.BLOOD_SAC);
    }

        public static void onTooltip(ItemStack itemStack, List<Component> tooltip) {
        var customData = itemStack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.getBooleanOr("BisonFur", false)) {
                tooltip.add(Component.translatable("item.alexsmobs.insulated_with_fur").withStyle(ChatFormatting.AQUA));
            }
        }
    }

        public static void onAddReloadListener() {
        AlexsMobs.LOGGER.info("Adding datapack listener capsid_recipes");
    }
}
