package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Maps;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
// NetworkHooks removed in NeoForge 1.21

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityElephant extends TamableAnimal implements ITargetsDroppedItems, IAnimatedEntity {

    private static final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> WOODEN_CHESTS = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.parse("c:chests/wooden"));

    public static final Animation ANIMATION_TRUMPET_0 = Animation.create(20);
    public static final Animation ANIMATION_TRUMPET_1 = Animation.create(30);
    public static final Animation ANIMATION_CHARGE_PREPARE = Animation.create(25);
    public static final Animation ANIMATION_STOMP = Animation.create(20);
    public static final Animation ANIMATION_FLING = Animation.create(25);
    public static final Animation ANIMATION_EAT = Animation.create(30);
    public static final Animation ANIMATION_BREAKLEAVES = Animation.create(20);
    protected static final EntityDimensions TUSKED_SIZE = EntityDimensions.fixed(3.7F, 3.75F);
    private static final EntityDataAccessor<Boolean> TUSKED = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CARPET_COLOR = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TRADER = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    public static final Map<DyeColor, Item> DYE_COLOR_ITEM_MAP = Util.make(Maps.newHashMap(), (map) -> {
        map.put(DyeColor.WHITE, Items.WHITE_CARPET);
        map.put(DyeColor.ORANGE, Items.ORANGE_CARPET);
        map.put(DyeColor.MAGENTA, Items.MAGENTA_CARPET);
        map.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_CARPET);
        map.put(DyeColor.YELLOW, Items.YELLOW_CARPET);
        map.put(DyeColor.LIME, Items.LIME_CARPET);
        map.put(DyeColor.PINK, Items.PINK_CARPET);
        map.put(DyeColor.GRAY, Items.GRAY_CARPET);
        map.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_CARPET);
        map.put(DyeColor.CYAN, Items.CYAN_CARPET);
        map.put(DyeColor.PURPLE, Items.PURPLE_CARPET);
        map.put(DyeColor.BLUE, Items.BLUE_CARPET);
        map.put(DyeColor.BROWN, Items.BROWN_CARPET);
        map.put(DyeColor.GREEN, Items.GREEN_CARPET);
        map.put(DyeColor.RED, Items.RED_CARPET);
        map.put(DyeColor.BLACK, Items.BLACK_CARPET);
    });
    private static final Identifier TRADER_LOOT = Identifier.fromNamespaceAndPath("alexsmobs", "gameplay/trader_elephant_chest");
    public boolean forcedSit = false;
    public float prevSitProgress;
    public float sitProgress;
    public float prevStandProgress;
    public float standProgress;
    public int maxStandTime = 75;
    public boolean aiItemFlag = false;
    public SimpleContainer elephantInventory;
    private int animationTick;
    private Animation currentAnimation;
    private final boolean hasTuskedAttributes = false;
    private int standingTime = 0;
    @Nullable
    private EntityElephant caravanHead;
    @Nullable
    private EntityElephant caravanTail;
    private boolean hasChestVarChanged = false;
    private boolean hasChargedSpeed = false;
    private boolean charging;
    private int chargeCooldown = 0;
    private int chargingTicks = 0;
    @Nullable
    private UUID blossomThrowerUUID = null;
    private int despawnDelay = 47999;

    protected EntityElephant(EntityType type, Level world) {
        super(type, world);
        initElephantInventory();
        // setMaxUpStep removed in 1.21
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.TEMPT_RANGE, 10.0D).add(Attributes.MAX_HEALTH, 85.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9F).add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    @Nullable
    public static DyeColor getCarpetColor(ItemStack stack) {
        Block lvt_1_1_ = Block.byItem(stack.getItem());
        return lvt_1_1_ instanceof WoolCarpetBlock ? ((WoolCarpetBlock) lvt_1_1_).getColor() : null;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.ELEPHANT_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ELEPHANT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ELEPHANT_DIE;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, EntitySpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.elephantSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    private void initElephantInventory() {
        SimpleContainer animalchest = this.elephantInventory;
        this.elephantInventory = new SimpleContainer(54){

            public boolean stillValid(Player player) {
                return EntityElephant.this.isAlive() && EntityElephant.this.portalProcess == null /* TODO: isInsidePortal removed */;
            }
        };
        if (animalchest != null) {
            int i = Math.min(animalchest.getContainerSize(), this.elephantInventory.getContainerSize());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.elephantInventory.setItem(j, itemstack.copy());
                }
            }
        }
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
    }

    public int getMaxHeadYRot() {
        return super.getMaxHeadYRot();
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSitting() || this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getAnimationTick() < 10;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1D, true));
        this.goalSelector.addGoal(2, new EntityElephant.PanicGoal());
        this.goalSelector.addGoal(2, new ElephantAIVillagerRide(this, 1D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(this.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(AMTagRegistry.ELEPHANT_TAMEABLES)), false));
        this.goalSelector.addGoal(5, new ElephantAIForageLeaves(this));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1D));
        this.goalSelector.addGoal(7, new ElephantAIFollowCaravan(this, 0.5D));
        this.goalSelector.addGoal(8, new AvoidEntityGoal<>(this, Bee.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(9, new EntityElephant.AIWalkIdle(this, 0.5D));
        this.targetSelector.addGoal(1, new EntityElephant.HurtByTargetGoal().setAlertOthers());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && stack.is(AMTagRegistry.ELEPHANT_BREEDABLES);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!isBaby()) {
            this.playSound(AMSoundRegistry.ELEPHANT_WALK, 0.2F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                return (LivingEntity) passenger;
            }
        }
        return null;
    }

    @Nullable
    public AbstractVillager getControllingVillager() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof AbstractVillager) {
                return (AbstractVillager) passenger;
            }
        }
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TUSKED, false);
        builder.define(SITTING, false);
        builder.define(STANDING, false);
        builder.define(CHESTED, false);
        builder.define(TRADER, false);
        builder.define(CARPET_COLOR, -1);
    }

    public void tick() {
        super.tick();
        prevSitProgress = sitProgress;
        prevStandProgress = standProgress;

        if (isSitting()) {
            if (this.sitProgress < 5F)
                this.sitProgress++;
        } else {
            if (this.sitProgress > 0F)
                this.sitProgress--;
        }

        if (isStanding()) {
            if (this.standProgress < 5F)
                this.standProgress += 0.5F;
        } else {
            if (this.standProgress > 0F)
                this.standProgress -= 0.5F;
        }

        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + this.getRandom().nextInt(50);
        }
        if (isSitting() && isStanding()) {
            this.setStanding(false);
        }
        if (hasChestVarChanged && elephantInventory != null && !this.isChested()) {
            for (int i = 3; i < 18; i++) {
                if (!elephantInventory.getItem(i).isEmpty()) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        this.spawnAtLocation(serverLevel, elephantInventory.getItem(i), 1.0F);
                    }
                    elephantInventory.removeItemNoUpdate(i);
                }
            }
            hasChestVarChanged = false;
        }
        if (!hasTuskedAttributes && isTusked() && !isBaby()) {
            refreshDimensions();
        }
        if (hasTuskedAttributes && !isTusked() && !isBaby()) {
            refreshDimensions();
        }
        if (charging) {
            chargingTicks++;
        }
        if (!this.getMainHandItem().isEmpty() && this.canTargetItem(this.getMainHandItem())) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_EAT);
            }
            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() == 17) {
                this.eatItemEffect(this.getMainHandItem());
                if (this.getMainHandItem().is(AMTagRegistry.ELEPHANT_TAMEABLES) && !this.isTame() && (!isTusked() || isBaby()) && blossomThrowerUUID != null) {
                    if (this.getRandom().nextInt(3) == 0) {
                        this.setTame(true, true);
                        this.setOwnerReference(EntityReference.of(blossomThrowerUUID));
                        Player player = this.level() instanceof ServerLevel serverLevel ? serverLevel.getPlayerInAnyDimension(blossomThrowerUUID) : null;
                        if (player != null) {
                            this.tame(player);
                        }
                        for (Entity passenger : this.getPassengers()) {
                            passenger.removeVehicle();
                        }
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                this.heal(10);
            }
        }
        if (chargeCooldown > 0) {
            chargeCooldown--;
        }
        if (charging) {
            chargingTicks++;
        } else {
            chargingTicks = 0;
        }
        if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
            this.yBodyRot = getYRot();
            if (this.getAnimationTick() == 20) {
                this.charging = true;
            }
        }
        if (this.getControllingPassenger() != null && charging && chargingTicks > 100) {
            this.charging = false;
            this.chargeCooldown = 200;
        }
        LivingEntity target = this.getTarget();
        double maxAttackMod = 0.0F;
        if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
            Player rider = (Player) this.getControllingPassenger();
            if (rider.getLastHurtMob() != null && !this.isAlliedTo(rider.getLastHurtMob())) {
                UUID preyUUID = rider.getLastHurtMob().getUUID();
                if (!this.getUUID().equals(preyUUID)) {
                    target = rider.getLastHurtMob();
                    maxAttackMod = 4F;
                }
            }
        }
        if (!this.level().isClientSide() && target != null) {
            if (this.distanceTo(target) > this.getBbWidth() * 0.5F + 0.5F && this.getControllingPassenger() == null && this.isTusked() && this.hasLineOfSight(target) && this.getAnimation() == NO_ANIMATION && !charging && chargeCooldown == 0) {
                this.setAnimation(ANIMATION_CHARGE_PREPARE);
            }
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getControllingPassenger() == null) {
                this.lookAt(target, 360, 30);
                this.yBodyRot = getYRot();
                if (this.getAnimationTick() == 20) {
                    this.charging = true;
                }
            }
            if (this.distanceTo(target) < 10D && charging) {
                this.setAnimation(ANIMATION_FLING);
            }
            if (this.distanceTo(target) < 2.1D && charging) {
                target.knockback(1, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.needsSync = true;
                target.setDeltaMovement(target.getDeltaMovement().add(0, 0.7F, 0));
                if (this.level() instanceof ServerLevel serverLevel) {
                    target.hurtServer(serverLevel, this.damageSources().mobAttack(this), 2.4F * (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
                launch(target, true);
                this.charging = false;
                this.chargeCooldown = 400;
            }
            double dist = this.distanceTo(target);
            if (dist < 4.5D + maxAttackMod && this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 15) {
                target.knockback(1, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.setDeltaMovement(target.getDeltaMovement().add(0, 0.3F, 0));
                launch(target, false);
                if (this.level() instanceof ServerLevel serverLevel) {
                    target.hurtServer(serverLevel, this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
            }
            if (dist < 4.5D + maxAttackMod && this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 17) {
                target.knockback(0.3, target.getX() - this.getX(), target.getZ() - this.getZ());
                if (this.level() instanceof ServerLevel serverLevel) {
                    target.hurtServer(serverLevel, this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
            }
        }
        if (!this.level().isClientSide() && this.getTarget() == null && this.getControllingPassenger() == null) {
            charging = false;
        }
        if (charging && !hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.65F);
            hasChargedSpeed = true;
        }
        if (!charging && hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
            hasChargedSpeed = false;
        }
        if (!this.level().isClientSide() && this.getRandom().nextInt(400) == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_TRUMPET_0 : ANIMATION_TRUMPET_1);
        }
        if (this.getAnimation() == ANIMATION_TRUMPET_0 && this.getAnimationTick() == 8 || this.getAnimation() == ANIMATION_TRUMPET_1 && this.getAnimationTick() == 4) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(AMSoundRegistry.ELEPHANT_TRUMPET, this.getSoundVolume(), this.getVoicePitch());
        }
        if (this.isAlive() && charging) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                if (!(this.isTame() && isAlliedTo(entity)) && !(!this.isTame() && entity instanceof EntityElephant) && entity != this) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        entity.hurtServer(serverLevel, this.damageSources().mobAttack(this), 8.0F + this.getRandom().nextFloat() * 8.0F);
                    }
                    launch(entity, true);
                }
            }
            // setMaxUpStep removed in 1.21
        }else{
            // setMaxUpStep removed in 1.21
        }
        if (!isTame() && isTrader()) {
            if (!this.level().isClientSide()) {
                this.tryDespawn();
            }
        }
        if (this.getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget(null);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void aiStep() {
        super.aiStep();
        if (this.isBaby() && this.getEyeHeight() > this.getBbHeight()) {
            this.refreshDimensions();
        }
    }

    private boolean canDespawn() {
        return !this.isTame() && this.isTrader();
    }

    private void tryDespawn() {
        if (this.canDespawn()) {
            if (this.getControllingVillager() instanceof WanderingTrader) {
                int riderDelay = ((WanderingTrader) this.getControllingVillager()).getDespawnDelay();
                if (riderDelay > 0) {
                    this.despawnDelay = riderDelay;
                }
            }
            this.despawnDelay = this.despawnDelay - 1;
            if (this.despawnDelay <= 0) {
                this.dropLeash();
                this.elephantInventory.clearContent();
                if(this.getControllingVillager() != null){
                    this.getControllingVillager().remove(RemovalReason.DISCARDED);
                }
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.onGround()) {
            double d0 = e.getX() - this.getX();
            double d1 = e.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = huge ? 2F : 0.5F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    private void eatItemEffect(ItemStack heldItemMainhand) {
        this.gameEvent(GameEvent.EAT);
        this.playSound(SoundEvents.STRIDER_EAT, this.getVoicePitch(), this.getSoundVolume());
        for (int i = 0; i < 8 + this.getRandom().nextInt(3); i++) {
            double d2 = this.getRandom().nextGaussian() * 0.02D;
            double d0 = this.getRandom().nextGaussian() * 0.02D;
            double d1 = this.getRandom().nextGaussian() * 0.02D;
            float radius = this.getBbWidth() * 0.65F;
            float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
            double extraX = radius * Mth.sin(Mth.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand.getItem());
            if (heldItemMainhand.getItem() instanceof BlockItem) {
                data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) heldItemMainhand.getItem()).getBlock().defaultBlockState());
            }
            this.level().addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
        }
    }

    private boolean isChargePlayer(Entity controllingPassenger) {
        return true;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION && !this.charging) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_FLING : ANIMATION_STOMP);
        }
        return true;
    }


    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean owner = this.isTame() && isOwnedBy(player);
        InteractionResult type = super.mobInteract(player, hand);
        if (isChested() && player.isShiftKeyDown()) {
            this.openGUI(player);
            return InteractionResult.SUCCESS;
        } else if (canTargetItem(stack) && this.getMainHandItem().isEmpty()) {
            ItemStack rippedStack = stack.copy();
            rippedStack.setCount(1);
            stack.shrink(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
            if (rippedStack.is(AMTagRegistry.ELEPHANT_TAMEABLES)) {
                blossomThrowerUUID = player.getUUID();
            }
            return InteractionResult.SUCCESS;
        } else if (owner && stack.is(ItemTags.WOOL_CARPETS)) {
            DyeColor color = getCarpetColor(stack);
            if (color != this.getColor()) {
                if (this.getColor() != null) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        this.spawnAtLocation(serverLevel, this.getCarpetItemBeingWorn());
                    }
                }
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.LLAMA_SWAG.value(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if(!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer){
                    AMAdvancementTriggerRegistry.ELEPHANT_SWAG.trigger(serverPlayer);
                }
                stack.shrink(1);
                this.setColor(color);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        } else if (owner && this.getColor() != null && stack.getItem() instanceof net.minecraft.world.item.ShearsItem) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.getColor() != null) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    this.spawnAtLocation(serverLevel, this.getCarpetItemBeingWorn());
                }
            }
            this.setColor(null);
            return InteractionResult.SUCCESS;
        } else if (owner && !this.isChested() && stack.is(WOODEN_CHESTS)) {
            this.setChested(true);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        } else if (owner && isChested() && stack.getItem() instanceof net.minecraft.world.item.ShearsItem) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                this.spawnAtLocation(serverLevel, Blocks.CHEST);
                for (int i = 0; i < elephantInventory.getContainerSize(); i++) {
                    this.spawnAtLocation(serverLevel, elephantInventory.getItem(i));
                }
            }
            elephantInventory.clearContent();
            this.setChested(false);
            return InteractionResult.SUCCESS;
        } else if (owner && !this.isBaby() && type != InteractionResult.CONSUME) {
            if(!this.level().isClientSide()){
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    // getDimensions is now final in 1.21, removed override

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }


    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_TRUMPET_0, ANIMATION_TRUMPET_1, ANIMATION_CHARGE_PREPARE, ANIMATION_STOMP, ANIMATION_FLING, ANIMATION_EAT, ANIMATION_BREAKLEAVES};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public Item getCarpetItemBeingWorn() {
        if (this.getColor() != null) {
            return DYE_COLOR_ITEM_MAP.get(this.getColor());
        }
        return Items.AIR;
    }

    @Override
    protected void dropEquipment(ServerLevel serverLevel) {
        super.dropEquipment(serverLevel);
        if (this.isChested()) {
            this.spawnAtLocation(serverLevel, Blocks.CHEST);
            for (int i = 0; i < elephantInventory.getContainerSize(); i++) {
                this.spawnAtLocation(serverLevel, elephantInventory.getItem(i));
            }
            elephantInventory.clearContent();
            this.setChested(false);
        }
        if (!this.isTrader() && this.getColor() != null) {
            this.spawnAtLocation(serverLevel, this.getCarpetItemBeingWorn());
            this.setColor(null);
        }

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        EntityElephant baby = AMEntityRegistry.ELEPHANT.create(serverWorld, EntitySpawnReason.BREEDING);
        baby.setTusked(this.getNearestTusked(level(), 15) == null || this.getRandom().nextInt(2) == 0);
        return baby;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Tusked", this.isTusked());
        compound.putBoolean("ElephantSitting", this.isSitting());
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Trader", this.isTrader());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putBoolean("Tamed", this.isTame());
        compound.putInt("ChargeCooldown", this.chargeCooldown);
        compound.putInt("Carpet", this.entityData.get(CARPET_COLOR));
        compound.putInt("DespawnDelay", this.despawnDelay);
        if (elephantInventory != null) {
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess());
            List<CompoundTag> slotTags = new ArrayList<>();
            for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.elephantInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    DataResult<Tag> enc = ItemStack.CODEC.encodeStart(ops, itemstack);
                    Tag tag = enc.getOrThrow();
                    if (tag instanceof CompoundTag stackTag) {
                        stackTag.putByte("Slot", (byte) i);
                        slotTags.add(stackTag);
                    }
                }
            }
            compound.store("Items", CompoundTag.CODEC.listOf(), slotTags);
        }
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == MobEffects.WITHER) {
            return false;
        }
        return super.canBeAffected(potioneffectIn);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        this.setTame(compound.getBooleanOr("Tamed", false), false);
        this.setTusked(compound.getBooleanOr("Tusked", false));
        this.setStanding(compound.getBooleanOr("Standing", false));
        this.setOrderedToSit(compound.getBooleanOr("ElephantSitting", false));
        this.setChested(compound.getBooleanOr("Chested", false));
        this.setTrader(compound.getBooleanOr("Trader", false));
        this.forcedSit = compound.getBooleanOr("ForcedToSit", false);
        this.chargeCooldown = compound.getIntOr("ChargeCooldown", 0);
        this.entityData.set(CARPET_COLOR, compound.getIntOr("Carpet", -1));
        this.initElephantInventory();
        compound.read("Items", CompoundTag.CODEC.listOf()).ifPresent(list -> {
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess());
            for (CompoundTag slotTag : list) {
                int j = slotTag.getByteOr("Slot", (byte) 0) & 255;
                CompoundTag itemPart = slotTag.copy();
                itemPart.remove("Slot");
                ItemStack.CODEC.parse(ops, itemPart).result().ifPresent(stack -> this.elephantInventory.setItem(j, stack));
            }
        });
        if (compound.getInt("DespawnDelay").isPresent()) {
            this.despawnDelay = compound.getIntOr("DespawnDelay", 0);
        }

    }

    public boolean isChested() {
        return Boolean.valueOf(this.entityData.get(CHESTED).booleanValue());
    }

    public void setChested(boolean chested) {
        this.entityData.set(CHESTED, Boolean.valueOf(chested));
        this.hasChestVarChanged = true;
    }

    public boolean setSlot(int inventorySlot, @Nullable ItemStack itemStackIn) {
        int j = inventorySlot - 500 + 2;
        if (j >= 0 && j < this.elephantInventory.getContainerSize()) {
            this.elephantInventory.setItem(j, itemStackIn);
            return true;
        } else {
            return false;
        }
    }

    public void die(DamageSource cause) {
        super.die(cause);
        if (elephantInventory != null && this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = elephantInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    this.spawnAtLocation(serverLevel, itemstack, 0.0F);
                }
            }
        }
    }


    public boolean isStanding() {
        return this.entityData.get(STANDING);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, Boolean.valueOf(standing));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    @Nullable
    public DyeColor getColor() {
        int lvt_1_1_ = this.entityData.get(CARPET_COLOR);
        return lvt_1_1_ == -1 ? null : DyeColor.byId(lvt_1_1_);
    }

    public void setColor(@Nullable DyeColor color) {
        this.entityData.set(CARPET_COLOR, color == null ? -1 : color.getId());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        if (spawnDataIn instanceof AgeableMob.AgeableMobGroupData) {
            AgeableMob.AgeableMobGroupData lvt_6_1_ = (AgeableMob.AgeableMobGroupData) spawnDataIn;
            if (lvt_6_1_.getGroupSize() == 0) {
                this.setTusked(true);
            }
        }else{
            this.setTusked(this.getRandom().nextBoolean());
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
    }

    @Nullable
    public EntityElephant getNearestTusked(LevelAccessor world, double dist) {
        List<? extends EntityElephant> list = world.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(dist, dist / 2, dist));
        if (list.isEmpty()) {
            return null;
        }
        EntityElephant elephant1 = null;
        double d0 = Double.MAX_VALUE;
        for (EntityElephant elephant : list) {
            if (elephant.isTusked()) {
                double d1 = this.distanceToSqr(elephant);
                if (!(d1 > d0)) {
                    d0 = d1;
                    elephant1 = elephant;
                }
            }
        }
        return elephant1;
    }

    public boolean isTusked() {
        return this.entityData.get(TUSKED);
    }

    public void setTusked(boolean tusked) {
        boolean prev = isTusked();
        if (!prev && tusked) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(110.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(15.0D);
            this.setHealth(150.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(85.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0D);
        }
        this.entityData.set(TUSKED, tusked);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.is(AMTagRegistry.ELEPHANT_FOODSTUFFS);
    }

    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.level() instanceof ServerLevel serverLevel) {
            this.spawnAtLocation(serverLevel, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        Entity itemThrower = e.getOwner();
        if (duplicate.is(AMTagRegistry.ELEPHANT_TAMEABLES) && itemThrower != null) {
            blossomThrowerUUID = itemThrower.getUUID();
        } else {
            blossomThrowerUUID = null;
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        this.aiItemFlag = false;
    }

    @Override
    public void onFindTarget(ItemEntity e) {
        this.aiItemFlag = true;
    }

    public void addElephantLoot(@Nullable Player player, int seed) {
        if (this.level().getServer() != null) {
            LootTable loottable = this.level().getServer().reloadableRegistries().getLootTable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, TRADER_LOOT));

            LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel) this.level()));

            loottable.fill(this.elephantInventory, lootcontext$builder.create(LootContextParamSets.EMPTY), seed);
        }

    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(EntityElephant caravanHeadIn) {
        this.caravanHead = caravanHeadIn;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTrail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public EntityElephant getCaravanHead() {
        return this.caravanHead;
    }

    public double getMaxDistToItem() {
        return Math.pow(this.getBbWidth() + 3.0F, 2.0F);
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            float standAdd = -0.3F * standProgress;
            float scale = this.isBaby() ? 0.5F : this.isTusked() ? 1.1F : 1.0F;
            float sitAdd = -0.065F * sitProgress;
            float scaleY = scale * (2.4F * sitAdd - 0.4F * standAdd);
            if (passenger instanceof AbstractVillager) {
                AbstractVillager villager = (AbstractVillager) passenger;
                scaleY -= 0.3F;
            }
            float radius = scale * (0.5F + standAdd);
            float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
                float sinWave = Mth.sin((float) (Math.PI * (this.getAnimationTick() / 25F)));
                radius += sinWave * 0.2F * scale;
            }
            if (this.getAnimation() == ANIMATION_STOMP) {
                float sinWave = Mth.sin((float) (Math.PI * (this.getAnimationTick() / 20F)));
                radius -= sinWave * 1.0F * scale;
                scaleY += sinWave * 0.7F * scale;
            }
            double extraX = radius * Mth.sin(Mth.PI + angle);
            double extraZ = radius * Mth.cos(angle);

            Vec3 attach = passenger.getVehicleAttachmentPoint(this);
            moveFunc.accept(passenger, this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + scaleY - attach.y, this.getZ() + extraZ);
        }
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        float standAdd = -0.3F * standProgress;
        float scale = this.isBaby() ? 0.5F : this.isTusked() ? 1.1F : 1.0F;
        float sitAdd = -0.065F * sitProgress;
        float scaleY = scale * (2.4F * sitAdd - 0.4F * standAdd);
        if (passenger instanceof AbstractVillager) {
            scaleY -= 0.3F;
        }
        float radius = scale * (0.5F + standAdd);
        float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
        if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
            float sinWave = Mth.sin((float) (Math.PI * (this.getAnimationTick() / 25F)));
            radius += sinWave * 0.2F * scale;
        }
        if (this.getAnimation() == ANIMATION_STOMP) {
            float sinWave = Mth.sin((float) (Math.PI * (this.getAnimationTick() / 20F)));
            radius -= sinWave * 1.0F * scale;
            scaleY += sinWave * 0.7F * scale;
        }
        double extraX = radius * Mth.sin(Mth.PI + angle);
        double extraZ = radius * Mth.cos(angle);
        return new Vec3(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + scaleY, this.getZ() + extraZ);
    }

    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        if (player.zza != 0) {
            float f = player.zza < 0.0F ? 0.5F : 1.0F;
            return new Vec3(player.xxa * 0.25F, 0.0D, player.zza * 0.5F * f);
        } else {
            this.setSprinting(false);
        }
        return Vec3.ZERO;
    }

    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if(player.zza != 0 || player.xxa != 0){
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
            // setMaxUpStep removed in 1.21
            this.getNavigation().stop();
            this.setTarget(null);
            this.setSprinting(true);
        }
    }

    protected float getRiddenSpeed(Player rider) {
        return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }

    public double getPassengersRidingOffset() {
        float scale = this.isBaby() ? 0.5F : this.isTusked() ? 1.1F : 1.0F;
        float f = Math.min(0.25F, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        float sitAdd = 0.01F * this.sitProgress;
        float standAdd = 0.07F * this.standProgress;
        return (double) this.getBbHeight() - 0.05F - scale * ((double) (0.1F * Mth.cos(f1 * 1.4F) * 1.4F * f) + sitAdd + standAdd);
    }

    @Override
    protected boolean considersEntityAsAlly(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }

        return super.considersEntityAsAlly(entityIn);
    }

    public void travel(Vec3 vec3d) {
        if (this.isSitting()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void openGUI(Player playerEntity) {
        if (!this.level().isClientSide() && (!this.hasPassenger(playerEntity))) {
            ((ServerPlayer) playerEntity).openMenu( new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                    return ChestMenu.sixRows(p_createMenu_1_, p_createMenu_2_, elephantInventory);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("entity.alexsmobs.elephant.chest");
                }
            });
        }
    }

    public boolean isTrader() {
        return this.entityData.get(TRADER);
    }

    public void setTrader(boolean trader) {
        this.entityData.set(TRADER, trader);
    }

    public boolean triggerCharge(ItemStack stack) {
        if (this.getControllingPassenger() != null && chargeCooldown == 0 && !charging && this.getAnimation() == NO_ANIMATION && this.isTusked()) {
            this.setAnimation(ANIMATION_CHARGE_PREPARE);
            this.eatItemEffect(stack);
            this.heal(2);
            return true;
        }
        return false;
    }

    public boolean canSpawnWithTraderHere() {
        return this.level().isLoaded(this.blockPosition()) && this.checkSpawnObstruction(level()) && level().isEmptyBlock(this.blockPosition().above(4));
    }

    private class AIWalkIdle extends RandomStrollGoal {
        public AIWalkIdle(EntityElephant e, double v) {
            super(e, v);
        }

        public boolean canUse() {
            this.interval = EntityElephant.this.isTusked() || !EntityElephant.this.inCaravan() ? 50 : 120;
            return super.canUse();
        }

        @Nullable
        protected Vec3 getPosition() {
            return LandRandomPos.getPos(this.mob, EntityElephant.this.isTusked() || !EntityElephant.this.inCaravan() ? 25 : 10, 7);
        }

    }

    class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(EntityElephant.this);
        }

        public void start() {
            if (EntityElephant.this.isBaby() || !EntityElephant.this.isTusked()) {
                this.alertOthers();
                this.stop();
            } else {
                super.start();
            }
        }

        protected void alertOther(Mob mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityElephant && (!mobIn.isBaby() || !((EntityElephant) mobIn).isTusked())) {
                super.alertOther(mobIn, targetIn);
            }

        }
    }

    class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(EntityElephant.this, 1.0D);
        }

        public boolean canUse() {
            return (EntityElephant.this.isBaby() || !EntityElephant.this.isTusked() || EntityElephant.this.isOnFire()) && super.canUse();
        }
    }
}
