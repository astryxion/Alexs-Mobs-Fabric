package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFleeLight;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.AMEntityHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityCockroach extends Animal implements Shearable, ITargetsDroppedItems {

    public static final Identifier MARACA_LOOT = Identifier.fromNamespaceAndPath("alexsmobs", "entities/cockroach_maracas");
    public static final Identifier MARACA_HEADLESS_LOOT = Identifier.fromNamespaceAndPath("alexsmobs", "entities/cockroach_maracas_headless");
    protected static final EntityDimensions STAND_SIZE = EntityDimensions.fixed(0.7F, 0.9F);
    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(EntityCockroach.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HEADLESS = SynchedEntityData.defineId(EntityCockroach.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MARACAS = SynchedEntityData.defineId(EntityCockroach.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> NEAREST_MUSICIAN = SynchedEntityData.defineId(EntityCockroach.class, AMEntityRegistry.OPTIONAL_UUID_SERIALIZER);
    private static final EntityDataAccessor<Boolean> BREADED = SynchedEntityData.defineId(EntityCockroach.class, EntityDataSerializers.BOOLEAN);
    public int randomWingFlapTick = 0;
    public float prevDanceProgress;
    public float danceProgress;
    private boolean prevStand = false;
    private boolean isJukeboxing;
    private BlockPos jukeboxPosition;
    private int laCucarachaTimer = 0;
    public int timeUntilNextEgg = this.random.nextInt(24000) + 24000;

    public EntityCockroach(EntityType type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.TEMPT_RANGE, 10.0D).add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    public static boolean isValidLightLevel(ServerLevelAccessor p_223323_0_, BlockPos p_223323_1_, RandomSource p_223323_2_) {
        if (p_223323_0_.getBrightness(LightLayer.SKY, p_223323_1_) > p_223323_2_.nextInt(32)) {
            return false;
        } else {
            int lvt_3_1_ = p_223323_0_.getLevel().isThundering() ? p_223323_0_.getMaxLocalRawBrightness(p_223323_1_, 10) : p_223323_0_.getMaxLocalRawBrightness(p_223323_1_);
            return lvt_3_1_ <= p_223323_2_.nextInt(8);
        }
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, EntitySpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.cockroachSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canMonsterSpawnInLight(EntityType<? extends EntityCockroach> p_223325_0_, ServerLevelAccessor p_223325_1_, EntitySpawnReason p_223325_2_, BlockPos p_223325_3_, RandomSource p_223325_4_) {
        return isValidLightLevel(p_223325_1_, p_223325_3_, p_223325_4_) && checkMobSpawnRules(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
    }

    public static <T extends Mob> boolean canCockroachSpawn(EntityType<EntityCockroach> entityType, ServerLevelAccessor iServerWorld, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return reason == EntitySpawnReason.SPAWNER || !iServerWorld.canSeeSky(pos) && pos.getY() <= 64 && canMonsterSpawnInLight(entityType, iServerWorld, reason, pos, random);
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !requiresCustomPersistence();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName() || this.isBreaded() || this.isDancing() || this.hasMaracas() || this.isHeadless();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.COCKROACH_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.COCKROACH_HURT;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(this.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(AMTagRegistry.COCKROACH_FOODSTUFFS)), false));
        this.goalSelector.addGoal(4, new AvoidEntityGoal(this, EntityCentipedeHead.class, 16, 1.3D, 1.0D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal(this, Player.class, 8, 1.3D, 1.0D) {
            public boolean canUse() {
                return !EntityCockroach.this.isBreaded() && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new AnimalAIFleeLight(this, 1.0D) {
            public boolean canUse() {
                return !EntityCockroach.this.isBreaded() && super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0D, 80));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean prev = super.hurtServer(level, source, amount);
        if (prev) {
            randomWingFlapTick = 5 + random.nextInt(15);
            if (this.getHealth() <= 1.0F && amount > 0 && !this.isHeadless() && this.getRandom().nextInt(3) == 0) {
                this.setHeadless(true);
                for (int i = 0; i < 3; i++) {
                    level.sendParticles(ParticleTypes.SNEEZE, this.getRandomX(0.52F), this.getY(1D), this.getRandomZ(0.52F), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        return prev;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.COCKROACH_BREEDABLES);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Maracas", this.hasMaracas());
        compound.putBoolean("Dancing", this.isDancing());
        compound.putBoolean("Breaded", this.isBreaded());
        compound.putInt("EggTime", this.timeUntilNextEgg);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        this.setMaracas(compound.getBooleanOr("Maracas", false));
        this.setDancing(compound.getBooleanOr("Dancing", false));
        this.setBreaded(compound.getBooleanOr("Breaded", false));
        compound.getInt("EggTime").ifPresent(v -> this.timeUntilNextEgg = v);
    }

    @Override
    protected void dropFromLootTable(ServerLevel level, DamageSource source, boolean playerKilled) {
        if (this.hasMaracas()) {
            this.dropFromLootTable(level, source, playerKilled, ResourceKey.create(Registries.LOOT_TABLE, this.isHeadless() ? MARACA_HEADLESS_LOOT : MARACA_LOOT));
        } else {
            super.dropFromLootTable(level, source, playerKilled);
        }
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return 0.5F - Math.max(worldIn.getBrightness(LightLayer.BLOCK, pos), worldIn.getBrightness(LightLayer.SKY, pos));
    }

    // getDimensions is now final in 1.21, removed override

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        return source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypeTags.IS_EXPLOSION) || source.getMsgId().equals("anvil") || super.isInvulnerableTo(level, source);
    }

    public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
        ItemStack lvt_3_1_ = p_230254_1_.getItemInHand(p_230254_2_);
        if (AMEntityHooks.shearWithShears(this, p_230254_1_, p_230254_2_, lvt_3_1_)) {
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
       if (lvt_3_1_.getItem() == AMItemRegistry.MARACA && this.isAlive() && !this.hasMaracas()) {
            this.setMaracas(true);
            lvt_3_1_.shrink(1);
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        } else if (lvt_3_1_.getItem() != AMItemRegistry.MARACA && this.isAlive() && this.hasMaracas()) {
            this.setMaracas(false);
            this.setDancing(false);
            AMEntityHooks.drop(this, new ItemStack(AMItemRegistry.MARACA));
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        } else {
            return super.mobInteract(p_230254_1_, p_230254_2_);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DANCING, false);
        builder.define(HEADLESS, false);
        builder.define(MARACAS, false);
        builder.define(NEAREST_MUSICIAN, Optional.empty());
        builder.define(BREADED, false);
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(DANCING, dancing);
    }

    public boolean isHeadless() {
        return this.entityData.get(HEADLESS);
    }

    public void setHeadless(boolean head) {
        this.entityData.set(HEADLESS, head);
    }

    public boolean hasMaracas() {
        return this.entityData.get(MARACAS);
    }

    public void setMaracas(boolean head) {
        this.entityData.set(MARACAS, head);
    }

    public boolean isBreaded() {
        return this.entityData.get(BREADED);
    }

    public void setBreaded(boolean breaded) {
        this.entityData.set(BREADED, breaded);
    }

    @Nullable
    public UUID getNearestMusicianId() {
        return this.entityData.get(NEAREST_MUSICIAN).orElse(null);
    }

    public void tick() {
        super.tick();
        prevDanceProgress = danceProgress;
        final boolean dance = this.isJukeboxing || isDancing();
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 3.46D) || !this.level().getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.isJukeboxing = false;
            this.jukeboxPosition = null;
        }
        if (this.getEyeHeight() > this.getBbHeight()) {
            this.refreshDimensions();
        }

        if (dance) {
            if (danceProgress < 5F)
                danceProgress++;
        } else {
            if (danceProgress > 0F)
                danceProgress--;
        }

        if (!this.onGround() || random.nextInt(200) == 0) {
            randomWingFlapTick = 5 + random.nextInt(15);
        }
        if (randomWingFlapTick > 0) {
            randomWingFlapTick--;
        }
        if (prevStand != dance) {
            if (hasMaracas()) {
                tellOthersImPlayingLaCucaracha();
            }
            this.refreshDimensions();
        }
        if (!hasMaracas()) {
            Entity musician = this.getNearestMusician();
            if (musician != null) {
                if (!musician.isAlive() || this.distanceTo(musician) > 10 || musician instanceof EntityCockroach && !((EntityCockroach) musician).hasMaracas()) {
                    this.setNearestMusician(null);
                    this.setDancing(false);
                } else {
                    this.setDancing(true);
                }
            }
        }
        if (hasMaracas()) {
            laCucarachaTimer++;
            if (laCucarachaTimer % 20 == 0 && random.nextFloat() < 0.3F) {
                tellOthersImPlayingLaCucaracha();
            }
            this.setDancing(true);
            if (!this.isSilent()) {
                this.level().broadcastEntityEvent(this, (byte) 67);
            }
        } else {
            laCucarachaTimer = 0;
        }
        if (!this.level().isClientSide() && this.isAlive() && !this.isBaby() && --this.timeUntilNextEgg <= 0) {
           ItemEntity dropped = AMEntityHooks.drop(this, AMItemRegistry.COCKROACH_OOTHECA);
           if(dropped != null){
               dropped.setDefaultPickUpDelay();
           }
            this.timeUntilNextEgg = this.random.nextInt(24000) + 24000;

        }
        prevStand = dance;
    }

    private void tellOthersImPlayingLaCucaracha() {
        List<EntityCockroach> list = this.level().getEntitiesOfClass(EntityCockroach.class, this.getMusicianDistance(), EntitySelector.NO_SPECTATORS);
        for (EntityCockroach roach : list) {
            if (!roach.hasMaracas()) {
                roach.setNearestMusician(this.getUUID());
            }
        }
    }

    private AABB getMusicianDistance() {
        return this.getBoundingBox().inflate(10, 10, 10);
    }
    public void handleEntityEvent(byte id) {
        if (id == 67) {
            AlexsMobs.PROXY.onEntityStatus(this, id);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public Entity getNearestMusician() {
        final UUID id = getNearestMusicianId();
        if (id != null && !this.level().isClientSide()) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

    public void setNearestMusician(@Nullable UUID uniqueId) {
        this.entityData.set(NEAREST_MUSICIAN, Optional.ofNullable(uniqueId));
    }
    public void setRecordPlayingNearby(BlockPos pos, boolean isPartying) {
        this.jukeboxPosition = pos;
        this.isJukeboxing = isPartying;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        final EntityCockroach roach = AMEntityRegistry.COCKROACH.create(serverWorld, EntitySpawnReason.BREEDING);
        roach.setBreaded(true);
        return roach;
    }

    public boolean readyForShearing() {
        return this.isAlive() && !this.isBaby() && !isHeadless();
    }

    public boolean isShearable(@javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos) {
        return readyForShearing();
    }

    @Override
    public void shear(ServerLevel serverLevel, SoundSource category, ItemStack item) {
        this.hurtServer(serverLevel, this.damageSources().generic(), 0F);
        serverLevel.playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
        this.gameEvent(GameEvent.ENTITY_INTERACT);
        this.setHeadless(true);
        AMEntityHooks.drop(this, new ItemStack(AMItemRegistry.COCKROACH_WING_FRAGMENT));
    }

    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return Collections.emptyList();
        }
        this.shear(serverLevel, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, item);
        for (int i = 0; i < 3; i++) {
            serverLevel.sendParticles(ParticleTypes.SNEEZE, this.getRandomX(0.52F), this.getY(1D), this.getRandomZ(0.52F), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.has(net.minecraft.core.component.DataComponents.FOOD) || stack.is(AMTagRegistry.COCKROACH_BREEDABLES);
    }

    public void travel(Vec3 vec3d) {
        if (this.isDancing() || danceProgress > 0) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }


    @Override
    public void onGetItem(ItemEntity e) {
        if (e.getItem().getItem() == AMItemRegistry.MARACA) {
            this.setMaracas(true);
        } else {
            ItemStackTemplate remainderTemplate = e.getItem().getCraftingRemainder();
            if (remainderTemplate != null) {
                ItemStack remainder = remainderTemplate.create();
                if (!remainder.isEmpty() && this.level() instanceof ServerLevel serverLevel) {
                    this.spawnAtLocation(serverLevel, remainder);
                }
            }
            this.heal(5);
            if (e.getItem().is(AMTagRegistry.COCKROACH_FOODSTUFFS)) {
                this.setBreaded(true);
            }
        }
    }
}
