package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ILeftClick;
import com.github.alexthe666.alexsmobs.item.ItemGhostlyPickaxe;
import com.github.alexthe666.alexsmobs.message.MessageSwingArm;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import com.github.alexthe666.alexsmobs.misc.ItemsForEmeraldsTrade;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.github.alexthe666.alexsmobs.world.BeachedCachalotWhaleSpawner;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ServerEvents {

    /** Simple triple for teleport deferral (replaces org.antlr Triple). */
    public record Triple<A, B, C>(A a, B b, C c) {}
    /** Fabric 1.20.1: no AFTER_DAMAGE event; queue (entity, source, amount) and process at end of tick (1:1). */
    private static final Queue<AfterDamageEntry> DEFERRED_AFTER_DAMAGE = new ConcurrentLinkedQueue<>();
    private record AfterDamageEntry(LivingEntity entity, DamageSource source, float amount) {}

    private static void enqueueAfterDamage(LivingEntity entity, DamageSource source, float amount) {
        DEFERRED_AFTER_DAMAGE.add(new AfterDamageEntry(entity, source, amount));
    }

    /** Fabric 1.20.1: Mob.goalSelector/targetSelector are protected; use reflection for 1:1 behavior. */
    private static GoalSelector getMobGoalSelector(Mob mob) {
        try {
            java.lang.reflect.Field f = Mob.class.getDeclaredField("goalSelector");
            f.setAccessible(true);
            return (GoalSelector) f.get(mob);
        } catch (Exception ex) {
            return null;
        }
    }
    private static GoalSelector getMobTargetSelector(Mob mob) {
        try {
            java.lang.reflect.Field f = Mob.class.getDeclaredField("targetSelector");
            f.setAccessible(true);
            return (GoalSelector) f.get(mob);
        } catch (Exception ex) {
            return null;
        }
    }

    private static void runDeferredAfterDamage() {
        AfterDamageEntry e;
        while ((e = DEFERRED_AFTER_DAMAGE.poll()) != null) {
            LivingEntity entity = e.entity();
            DamageSource source = e.source();
            float amount = e.amount();
            if (!entity.isAlive()) continue;
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (amount > 0 && attacker.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.SOULSTEAL)) && attacker.getEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.SOULSTEAL)) != null) {
                    int lvl = attacker.getEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.SOULSTEAL)).getAmplifier() + 1;
                    if (attacker.getHealth() < attacker.getMaxHealth() && ThreadLocalRandom.current().nextFloat() < (0.25F + (lvl * 0.25F))) {
                        attacker.heal(Math.min(amount / 2F * lvl, 2 + 2 * lvl));
                    }
                }
                if (entity instanceof Player player && attacker.distanceTo(player) < attacker.getBbWidth() + player.getBbWidth() + 0.5F && player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL) {
                    attacker.hurt(attacker.damageSources().thorns(player), 1F);
                    attacker.knockback(0.5F, Mth.sin((attacker.getYRot() + 180) * Mth.DEG_TO_RAD), -Mth.cos((attacker.getYRot() + 180) * Mth.DEG_TO_RAD));
                }
            }
            if (!entity.getUseItem().isEmpty() && source.getEntity() != null && source.getEntity() instanceof LivingEntity living) {
                if (entity.getUseItem().getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP) {
                    boolean flag = false;
                    if (living.distanceTo(entity) <= 4 && !living.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.EXSANGUINATION))) {
                        living.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.EXSANGUINATION), 60, 2));
                        flag = true;
                    }
                    if (entity.isInWaterOrBubble()) {
                        entity.setAirSupply(Math.min(entity.getMaxAirSupply(), entity.getAirSupply() + 150));
                        flag = true;
                    }
                    if (flag) {
                        entity.getUseItem().hurtAndBreak(1, entity, entity.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                    }
                }
            }
        }
    }

    /** Vanilla/Fabric: give stack to player, drop remainder (replaces ItemHandlerHelper.giveItemToPlayer). */
    public static void giveItemToPlayer(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, true);
        }
    }

    /** Fabric: register server/world/entity events here (replaces Forge EVENT_BUS). */
    public static void register() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(AlexsMobs.PROXY.getCapsidRecipeManager());
        AlexsMobs.LOGGER.info("Adding datapack listener capsid_recipes");

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                onLevelTick(level);
            }
            runDeferredAfterDamage();
        });


        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            onPlayerLoggedIn(player);
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getType() == EntityType.SQUID && !entity.level().isClientSide && source.is(DamageTypeTags.IS_LIGHTNING)) {
                ServerLevel level = (ServerLevel) entity.level();
                EntityGiantSquid squid = AMEntityRegistry.GIANT_SQUID.create(level);
                squid.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                squid.finalizeSpawn(level, level.getCurrentDifficultyAt(squid.blockPosition()), MobSpawnType.CONVERSION, null);
                if (entity.hasCustomName()) {
                    squid.setCustomName(entity.getCustomName());
                    squid.setCustomNameVisible(entity.isCustomNameVisible());
                }
                squid.setBlue(true);
                squid.setPersistenceRequired();
                level.addFreshEntityWithPassengers(squid);
                entity.discard();
                return false;
            }
            if (entity instanceof EntityEmu emu && source.getDirectEntity() instanceof Projectile projectile && !entity.level().isClientSide) {
                if (projectile instanceof AbstractArrow arrow) {
                    try {
                        java.lang.reflect.Method m = AbstractArrow.class.getDeclaredMethod("setPierceLevel", byte.class);
                        m.setAccessible(true);
                        m.invoke(arrow, (byte)0);
                    } catch (Exception ignored) {}
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
                    emu.hasImpulse = true;
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
            if (!entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty() && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.EMU_LEGGINGS) {
                if (source.is(DamageTypeTags.IS_PROJECTILE) && entity.getRandom().nextFloat() < AMConfig.emuPantsDodgeChance) {
                    return false;
                }
            }
            if (amount > 0) enqueueAfterDamage(entity, source, amount);
            return true;
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.DEBILITATING_STING)) && entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.DEBILITATING_STING)) != null && entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.DEBILITATING_STING)).getAmplifier() > 0) {
                if (entity instanceof Mob mob) {
                    mob.setPersistenceRequired();
                }
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (VineLassoUtil.hasLassoData(entity)) {
                VineLassoUtil.lassoTo(null, entity);
                entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(AMItemRegistry.VINE_LASSO)));
            }
        });

        net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 2, factories -> {
            factories.add(new EmeraldsForItemsTrade(AMItemRegistry.AMBERGRIS, 20, 3, 4));
        });
        if (AMConfig.wanderingTraderOffers) {
            net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.ANIMAL_DICTIONARY, 4, 1, 2, 1));
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.ACACIA_BLOSSOM, 3, 2, 2, 1));
                if (AMConfig.cockroachSpawnWeight > 0) factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.COCKROACH_OOTHECA, 2, 1, 2, 1));
                if (AMConfig.blobfishSpawnWeight > 0) factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOBFISH_BUCKET, 4, 1, 3, 1));
                if (AMConfig.crocodileSpawnWeight > 0) factories.add(new ItemsForEmeraldsTrade(AMBlockRegistry.CROCODILE_EGG.asItem(), 6, 1, 2, 1));
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.BEAR_FUR, 1, 1, 2, 1));
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.CROCODILE_SCUTE, 5, 1, 2, 1));
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.ROADRUNNER_FEATHER, 1, 2, 2, 2));
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.MOSQUITO_LARVA, 1, 3, 5, 1));
            });
            net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper.registerWanderingTraderOffers(2, factories -> {
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.SOMBRERO, 20, 1, 1, 1));
                factories.add(new ItemsForEmeraldsTrade(AMBlockRegistry.BANANA_PEEL, 1, 2, 1, 1));
                factories.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOOD_SAC, 5, 2, 3, 1));
            });
        }

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof WanderingTrader trader && AMConfig.elephantTraderSpawnChance > 0) {
                Biome biome = serverWorld.getBiome(entity.blockPosition()).value();
                if (RAND.nextFloat() <= AMConfig.elephantTraderSpawnChance && (!AMConfig.limitElephantTraderBiomes || biome.getBaseTemperature() >= 1.0F)) {
                    ChunkPos chunkPos = new ChunkPos(trader.blockPosition());
                    if (serverWorld.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z) != null) {
                        EntityElephant elephant = AMEntityRegistry.ELEPHANT.create(serverWorld);
                        elephant.copyPosition(trader);
                        if (elephant.canSpawnWithTraderHere()) {
                            elephant.setTrader(true);
                            elephant.setChested(true);
                            serverWorld.addFreshEntity(elephant);
                            trader.startRiding(elephant, true);
                        }
                        elephant.addElephantLoot(null, RAND.nextInt());
                    }
                }
            }
            try {
                if (AMConfig.spidersAttackFlies && entity instanceof Spider spider) {
                    GoalSelector ts = getMobTargetSelector(spider);
                    if (ts != null) ts.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityFly.class, 1, true, false, null));
                } else if (AMConfig.wolvesAttackMoose && entity instanceof Wolf wolf) {
                    GoalSelector ts = getMobTargetSelector(wolf);
                    if (ts != null) ts.addGoal(6, new NonTameRandomTargetGoal<>(wolf, EntityMoose.class, false, null));
                } else if (AMConfig.polarBearsAttackSeals && entity instanceof PolarBear bear) {
                    GoalSelector ts = getMobTargetSelector(bear);
                    if (ts != null) ts.addGoal(6, new NearestAttackableTargetGoal<>(bear, EntitySeal.class, 15, true, true, null));
                } else if (entity instanceof Creeper creeper) {
                    GoalSelector ts = getMobTargetSelector(creeper);
                    if (ts != null) {
                        ts.addGoal(3, new AvoidEntityGoal<>(creeper, EntitySnowLeopard.class, 6.0F, 1.0D, 1.2D));
                        ts.addGoal(3, new AvoidEntityGoal<>(creeper, EntityTiger.class, 6.0F, 1.0D, 1.2D));
                    }
                } else if (AMConfig.catsAndFoxesAttackJerboas && (entity instanceof Fox || entity instanceof Cat || entity instanceof Ocelot)) {
                    Mob mb = (Mob) entity;
                    GoalSelector ts = getMobTargetSelector(mb);
                    if (ts != null) ts.addGoal(6, new NearestAttackableTargetGoal<>(mb, EntityJerboa.class, 45, true, true, null));
                } else if (AMConfig.bunfungusTransformation && entity instanceof Rabbit rabbit) {
                    GoalSelector gs = getMobGoalSelector(rabbit);
                    if (gs != null) gs.addGoal(3, new TemptGoal(rabbit, 1.0D, Ingredient.of(AMItemRegistry.MUNGAL_SPORES), false));
                } else if (AMConfig.dolphinsAttackFlyingFish && entity instanceof Dolphin dolphin) {
                    GoalSelector ts = getMobTargetSelector(dolphin);
                    if (ts != null) ts.addGoal(2, new NearestAttackableTargetGoal<>(dolphin, EntityFlyingFish.class, 70, true, true, null));
                }
            } catch (Exception e) {
                AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
            }
        });

        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof LivingEntity living) {
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.MOOSE_HEADGEAR) {
                    living.knockback(1F, Mth.sin(player.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD));
                }
                if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.TIGERS_BLESSING)) && !entity.isAlliedTo(player) && !(entity instanceof EntityTiger)) {
                    AABB bb = new AABB(player.getX() - 32, player.getY() - 32, player.getZ() - 32, player.getX() + 32, player.getY() + 32, player.getZ() + 32);
                    for (EntityTiger tiger : world.getEntitiesOfClass(EntityTiger.class, bb, EntitySelector.ENTITY_STILL_ALIVE)) {
                        if (!tiger.isBaby()) tiger.setTarget(living);
                    }
                }
            }
            return InteractionResult.PASS;
        });

        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (AlexsMobs.isAprilFools() && player.getItemInHand(hand).is(Items.STICK) && !player.getCooldowns().isOnCooldown(Items.STICK)) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                if (state.is(Blocks.SAND)) {
                    world.setBlockAndUpdate(pos, AMBlockRegistry.SAND_CIRCLE.defaultBlockState());
                    player.gameEvent(GameEvent.BLOCK_PLACE);
                    player.playSound(SoundEvents.SAND_BREAK, 1, 1);
                    player.getCooldowns().addCooldown(Items.STICK, 30);
                    return InteractionResult.SUCCESS;
                }
                if (state.is(Blocks.RED_SAND)) {
                    world.setBlockAndUpdate(pos, AMBlockRegistry.RED_SAND_CIRCLE.defaultBlockState());
                    player.gameEvent(GameEvent.BLOCK_PLACE);
                    player.playSound(SoundEvents.SAND_BREAK, 1, 1);
                    player.getCooldowns().addCooldown(Items.STICK, 30);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });

        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof LivingEntity living) {
                if (!player.isShiftKeyDown() && VineLassoUtil.hasLassoData(living)) {
                    if (!world.isClientSide) entity.spawnAtLocation(new ItemStack(AMItemRegistry.VINE_LASSO));
                    VineLassoUtil.lassoTo(null, living);
                    return InteractionResult.SUCCESS;
                }
                if (!(entity instanceof Player) && !(entity instanceof EntityEndergrade) && living.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.ENDER_FLU)) && player.getItemInHand(hand).getItem() == Items.CHORUS_FRUIT) {
                    if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
                    entity.gameEvent(GameEvent.EAT);
                    entity.playSound(SoundEvents.GENERIC_EAT, 1.0F, 0.5F + player.getRandom().nextFloat());
                    if (player.getRandom().nextFloat() < 0.4F) {
                        living.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.ENDER_FLU));
                        Items.CHORUS_FRUIT.finishUsingItem(player.getItemInHand(hand).copy(), world, living);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (RainbowUtil.getRainbowType(living) > 0 && player.getItemInHand(hand).getItem() == Items.SPONGE) {
                    RainbowUtil.setRainbowType(living, 0);
                    if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
                    ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
                    if (!player.getInventory().add(wetSponge)) player.drop(wetSponge, true);
                    return InteractionResult.SUCCESS;
                }
                if (living instanceof Rabbit rabbit && player.getItemInHand(hand).getItem() == AMItemRegistry.MUNGAL_SPORES && AMConfig.bunfungusTransformation) {
                    var random = ThreadLocalRandom.current();
                    if (!world.isClientSide && random.nextFloat() < 0.15F) {
                        EntityBunfungus bunfungus = rabbit.convertTo(AMEntityRegistry.BUNFUNGUS, true);
                        if (bunfungus != null) {
                            world.addFreshEntity(bunfungus);
                            bunfungus.setTransformsIn(EntityBunfungus.MAX_TRANSFORM_TIME);
                        }
                    } else {
                        for (int i = 0; i < 2 + random.nextInt(2); i++) {
                            world.addParticle(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION, entity.getRandomX(0.7F), entity.getY(0.6F), entity.getRandomZ(0.7F), random.nextGaussian() * 0.02D, 0.05F + random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D);
                        }
                    }
                    if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });

        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() == Items.WHEAT && player.getVehicle() instanceof EntityElephant elephant) {
                if (elephant.triggerCharge(stack)) {
                    player.swing(hand);
                    if (!player.isCreative()) stack.shrink(1);
                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                }
            }
            if (stack.getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled) {
                HitResult hit = rayTrace(world, player, ClipContext.Fluid.SOURCE_ONLY);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockpos = ((BlockHitResult) hit).getBlockPos();
                    if (world.mayInteract(player, blockpos) && world.getFluidState(blockpos).is(FluidTags.LAVA)) {
                        player.gameEvent(GameEvent.ITEM_INTERACT_START);
                        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        player.igniteForSeconds(6);
                        if (!player.getInventory().add(new ItemStack(AMItemRegistry.LAVA_BOTTLE))) {
                            player.drop(new ItemStack(AMItemRegistry.LAVA_BOTTLE), true);
                        }
                        player.swing(hand);
                        if (!player.isCreative()) stack.shrink(1);
                        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                    }
                }
            }
            if (RainbowUtil.getRainbowType(player) > 0 && stack.is(Items.SPONGE)) {
                player.swing(InteractionHand.MAIN_HAND);
                RainbowUtil.setRainbowType(player, 0);
                if (!player.isCreative()) stack.shrink(1);
                ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
                if (!player.getInventory().add(wetSponge)) player.drop(wetSponge, true);
                return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
            }
            return InteractionResultHolder.pass(stack);
        });

        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player != null && player.isHolding(AMItemRegistry.GHOSTLY_PICKAXE) && ItemGhostlyPickaxe.shouldStoreInGhost(player, player.getMainHandItem())) {
                if (!world.isClientSide() && world instanceof ServerLevel serverLevel) {
                    ItemGhostlyPickaxe.breakBlockAndStoreInGhost(serverLevel, pos, state, blockEntity, player, player.getMainHandItem());
                }
                return false;
            }
            return true;
        });
    }

    private static void onLevelTick(ServerLevel level) {
        BEACHED_CACHALOT_WHALE_SPAWNER_MAP.computeIfAbsent(level, k -> new BeachedCachalotWhaleSpawner(level));
        BeachedCachalotWhaleSpawner spawner = BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(level);
        spawner.tick();
        if (!teleportPlayers.isEmpty()) {
            for (final var triple : teleportPlayers) {
                ServerPlayer player = triple.a;
                ServerLevel endpointWorld = triple.b;
                BlockPos endpoint = triple.c;
                final int heightFromMap = endpointWorld.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, endpoint.getX(), endpoint.getZ());
                endpoint = new BlockPos(endpoint.getX(), Math.max(heightFromMap, endpoint.getY()), endpoint.getZ());
                player.teleportTo(endpointWorld, endpoint.getX() + 0.5D, endpoint.getY() + 0.5D, endpoint.getZ() + 0.5D, player.getYRot(), player.getXRot());
                ChunkPos chunkpos = new ChunkPos(endpoint);
                endpointWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, player.getId());
                player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
            }
            teleportPlayers.clear();
        }
        AMWorldData data = AMWorldData.get(level);
        if (data != null) {
            data.tickPupfish();
        }
    }

    /** Called per-entity from LivingEntityMixin (server-only). Replaces full-world getEntitiesOfClass scan. */
    public static void onLivingEntityTick(LivingEntity entity) {
        onLivingTick(entity);
    }

    private static void onLivingTick(LivingEntity entity) {
        if (!entity.isUsingItem() && CHORUS_FRUIT_USERS_LAST_TICK.remove(entity.getId()) && entity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.ENDER_FLU)) && RAND.nextInt(3) == 0) {
            entity.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.ENDER_FLU));
        }
        if (entity.isUsingItem() && entity.getUseItem().getItem() == Items.CHORUS_FRUIT) {
            CHORUS_FRUIT_USERS_LAST_TICK.add(entity.getId());
        }
        if (entity instanceof Mob mob && mob.getTarget() != null) {
            LivingEntity target = mob.getTarget();
            if (MobType.getMobType(mob) == MobType.ARTHROPOD && target.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.BUG_PHEROMONES)) && mob.getLastHurtByMob() != target) {
                mob.setTarget(null);
            }
            if (MobType.getMobType(mob) == MobType.UNDEAD && !mob.getType().is(AMTagRegistry.IGNORES_KIMONO) && target.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO) && mob.getLastHurtByMob() != target) {
                mob.setTarget(null);
            }
        }
        if (entity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.DEBILITATING_STING)) && entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.DEBILITATING_STING)) != null && entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.DEBILITATING_STING)).getAmplifier() > 0 && entity instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
        if (entity instanceof Player player) {
            if (player.getEyeHeight() < player.getBbHeight() * 0.5D) {
                player.refreshDimensions();
            }
            if (player.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.CLINGING)) != null && EffectClinging.isUpsideDown(player)) {
                float minus = player.getBbHeight() - player.getEyeHeight();
                setLivingEyeHeight(player, minus);
            }
            if (player.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                var attributes = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS || (attributes != null && attributes.hasModifier(SAND_SPEED_BONUS_ID))) {
                    final boolean sand = player.level().getBlockState(getDownPos(player.blockPosition(), player.level())).is(BlockTags.SAND);
                    if (sand && (attributes != null && !attributes.hasModifier(SAND_SPEED_BONUS_ID))) {
                        if (attributes != null) attributes.addPermanentModifier(SAND_SPEED_BONUS);
                    }
                    if (player.tickCount % 25 == 0 && (player.getItemBySlot(EquipmentSlot.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS || !sand) && (attributes != null && attributes.hasModifier(SAND_SPEED_BONUS_ID))) {
                        if (attributes != null) attributes.removeModifier(SAND_SPEED_BONUS_ID);
                    }
                }
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.FRONTIER_CAP || (attributes != null && attributes.hasModifier(SNEAK_SPEED_BONUS_ID))) {
                    final boolean shift = player.isShiftKeyDown();
                    if (shift && (attributes != null && !attributes.hasModifier(SNEAK_SPEED_BONUS_ID))) {
                        if (attributes != null) attributes.addPermanentModifier(SNEAK_SPEED_BONUS);
                    }
                    if ((!shift || player.getItemBySlot(EquipmentSlot.HEAD).getItem() != AMItemRegistry.FRONTIER_CAP) && (attributes != null && attributes.hasModifier(SNEAK_SPEED_BONUS_ID))) {
                        if (attributes != null) attributes.removeModifier(SNEAK_SPEED_BONUS_ID);
                    }
                }
            }
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL && !player.isEyeInFluid(FluidTags.WATER)) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 310, 0, false, false, true));
            }
        }
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        net.minecraft.world.item.component.CustomData bootsData = boots.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (!boots.isEmpty() && bootsData != null && bootsData.copyTag().contains("BisonFur") && bootsData.copyTag().getBoolean("BisonFur")) {
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
        if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS) {
            if (entity.horizontalCollision && !entity.isInWater()) {
                entity.fallDistance = 0.0F;
                Vec3 motion = entity.getDeltaMovement();
                double d2 = 0.1D;
                if (entity.isShiftKeyDown() || !entity.getBlockStateOn().is(Blocks.SCAFFOLDING) && entity.isSuppressingSlidingDownLadder()) {
                    d2 = 0.0D;
                }
                motion = new Vec3(Mth.clamp(motion.x, -0.15F, 0.15F), d2, Mth.clamp(motion.z, -0.15F, 0.15F));
                entity.setDeltaMovement(motion);
            }
        }
        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SOMBRERO && !entity.level().isClientSide && AlexsMobs.isAprilFools() && entity.isInWaterOrBubble()) {
            RandomSource random = entity.getRandom();
            if (random.nextInt(245) == 0 && !EntitySeaBear.isMobSafe(entity)) {
                final int dist = 32;
                var nearbySeabears = entity.level().getEntitiesOfClass(EntitySeaBear.class, entity.getBoundingBox().inflate(dist, dist, dist));
                if (nearbySeabears.isEmpty()) {
                    EntitySeaBear bear = AMEntityRegistry.SEA_BEAR.create(entity.level());
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
                } else {
                    for (EntitySeaBear bear : nearbySeabears) {
                        bear.setTarget(entity);
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

    private static void onPlayerLoggedIn(ServerPlayer player) {
        if (AMConfig.giveBookOnStartup) {
            AMWorldData worldData = AMWorldData.get(player.serverLevel());
            if (worldData != null && !worldData.hasAnimalDictionaryBeenGranted(player.getUUID())) {
                giveItemToPlayer(player, new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY));
                final boolean isAlex = Objects.equals(player.getUUID(), ALEX_UUID);
                if (isAlex || Objects.equals(player.getUUID(), CARRO_UUID)) {
                    giveItemToPlayer(player, new ItemStack(AMItemRegistry.BEAR_DUST));
                }
                if (isAlex) {
                    giveItemToPlayer(player, new ItemStack(AMItemRegistry.NOVELTY_HAT));
                }
                worldData.markAnimalDictionaryGranted(player.getUUID());
            }
        }
    }

    /** Fabric 1.20.1: LivingEntity.setEyeHeight is protected; use reflection for 1:1 behavior. */
    private static void setLivingEyeHeight(LivingEntity living, float height) {
        try {
            java.lang.reflect.Method m = LivingEntity.class.getDeclaredMethod("setEyeHeight", float.class);
            m.setAccessible(true);
            m.invoke(living, height);
        } catch (Exception ignored) {}
    }
    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    public static final UUID CARRO_UUID = UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290");
    private static final ResourceLocation SAND_SPEED_BONUS_ID = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "roadrunner_speed_bonus");
    private static final ResourceLocation SNEAK_SPEED_BONUS_ID = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "frontier_cap_speed_bonus");
    private static final AttributeModifier SAND_SPEED_BONUS = new AttributeModifier(SAND_SPEED_BONUS_ID, 0.1, AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier SNEAK_SPEED_BONUS = new AttributeModifier(SNEAK_SPEED_BONUS_ID, 0.1, AttributeModifier.Operation.ADD_VALUE);
    private static final Map<ServerLevel, BeachedCachalotWhaleSpawner> BEACHED_CACHALOT_WHALE_SPAWNER_MAP = new HashMap<>();
    public static final ObjectList<Triple<ServerPlayer, ServerLevel, BlockPos>> teleportPlayers = new ObjectArrayList<>();
    private static final Set<Integer> CHORUS_FRUIT_USERS_LAST_TICK = new HashSet<>();

    private static final Random RAND = new Random();

    protected static BlockHitResult rayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        final float x = player.getXRot();
        final float y = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        final float f0 = -y * Mth.DEG_TO_RAD - Mth.PI;
        final float f1 = -x * Mth.DEG_TO_RAD;
        final float f2 = Mth.cos(f0);
        final float f3 = Mth.sin(f0);
        final float f4 = -Mth.cos(f1);
        final float f5 = Mth.sin(f1);
        final float f6 = f3 * f4;
        final float f7 = f2 * f4;
        final double d0 = 5.0D; // vanilla default block reach (Fabric has no ForgeMod.BLOCK_REACH)
        Vec3 vector3d1 = vector3d.add(f6 * d0, f5 * d0, f7 * d0);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    private static BlockPos getDownPos(BlockPos entered, LevelAccessor world) {
        int i = 0;
        while (world.isEmptyBlock(entered) && i < 3) {
            entered = entered.below();
            i++;
        }
        return entered;
    }

}