package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.network.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityCentipedeBody extends Mob implements IHurtableMultipart {

    private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(EntityCentipedeBody.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> BODY_XROT = SynchedEntityData.defineId(EntityCentipedeBody.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(EntityCentipedeBody.class, AMEntityRegistry.OPTIONAL_UUID_SERIALIZER);
    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityCentipedeBody.class, AMEntityRegistry.OPTIONAL_UUID_SERIALIZER);
    public EntityDimensions multipartSize;
    protected float radius;
    protected float angleYaw;
    protected float damageMultiplier = 1;
    private double prevHeight = 0;
    protected EntityCentipedeBody(EntityType type, Level worldIn) {
        super(type, worldIn);
        multipartSize = type.getDimensions();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getParent() != null;
    }


    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(level, source);
    }

    public boolean isNoGravity() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        // isInsidePortal removed in 1.21
        this.setDeltaMovement(Vec3.ZERO);
        if (this.tickCount > 1) {
            final Entity parent = getParent();
            refreshDimensions();
            if (parent != null && !this.level().isClientSide()) {
                if (parent instanceof final LivingEntity parentEntity) {
                    if ((parentEntity.hurtTime > 0 || parentEntity.deathTime > 0)) {
                        AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), 0.0F, ""));
                        this.hurtTime = parentEntity.hurtTime;
                        this.deathTime = parentEntity.deathTime;
                    }
                }
                if (parent.isRemoved()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            } else if (!this.level().isClientSide() && tickCount > 20) {
                remove(RemovalReason.DISCARDED);
            }
        }
    }

    public EntityCentipedeBody(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(t, parent.level());
        this.setParent(parent);
    }

    public void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        if (this.getParentId() != null) {
            compound.store("ParentUUID", UUIDUtil.CODEC, this.getParentId());
        }
        if (this.getChildId() != null) {
            compound.store("ChildUUID", UUIDUtil.CODEC, this.getChildId());
        }
        compound.putInt("BodyIndex", getBodyIndex());
        compound.putFloat("PartAngle", angleYaw);
        compound.putFloat("PartRadius", radius);
    }

    public void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        compound.read("ParentUUID", UUIDUtil.CODEC).ifPresent(this::setParentId);
        compound.read("ChildUUID", UUIDUtil.CODEC).ifPresent(this::setChildId);
        this.setBodyIndex(compound.getIntOr("BodyIndex", 0));
        this.angleYaw = compound.getFloatOr("PartAngle", 0.0F);
        this.radius = compound.getFloatOr("PartRadius", 0.0F);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PARENT_UUID, Optional.empty());
        builder.define(CHILD_UUID, Optional.empty());
        builder.define(BODYINDEX, 0);
        builder.define(BODY_XROT, 0F);
    }

    public Entity getParent() {
        final UUID id = getParentId();
        if (id != null && !this.level().isClientSide()) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

    public void setParent(Entity entity) {
        this.setParentId(entity.getUUID());
    }

    public Entity getChild() {
        final UUID id = getChildId();
        if (id != null && !this.level().isClientSide()) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

    @Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    @Override
    public boolean is(net.minecraft.world.entity.Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        final Entity parent = getParent();
        final float scaled = damage * this.damageMultiplier;
        boolean prev = false;
        if (parent != null) {
            if (parent instanceof LivingEntity living) {
                prev = living.hurtServer(level, source, scaled);
            } else {
                prev = parent.hurtOrSimulate(source, scaled);
            }
            if (prev) {
                AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), scaled, ""));
            }
        }
        return prev;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public void pushEntities() {
        final List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        final Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityCentipedeBody) && entity.isPushable()).forEach(entity -> entity.push(parent));
        }
    }

    @Override
    public boolean startRiding(Entity entityToRide, boolean force, boolean sendEventAndTriggers) {
        if (!(entityToRide instanceof AbstractMinecart || entityToRide instanceof Boat)) {
            return super.startRiding(entityToRide, force, sendEventAndTriggers);
        }
        return false;
    }

    public int getBodyIndex() {
        return this.entityData.get(BODYINDEX);
    }

    public void setBodyIndex(int index) {
        this.entityData.set(BODYINDEX, index);
    }

    @Nullable
    public UUID getParentId() {
        return this.entityData.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ARMOR, 6.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.5F).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public Vec3 tickMultipartPosition(int headId, float parentOffset, Vec3 parentPosition, float parentXRot, float ourYRot, boolean doHeight) {
        final float yDif = doHeight ? 1.0F - 0.95F * (float)Math.min(Math.abs(parentPosition.y - this.getY()), 1.0F) : 1F;
        final Vec3 parentFront = parentPosition.add(calcOffsetVec(yDif * parentOffset * this.getScale(), parentXRot, ourYRot));
        final Vec3 parentButt = parentPosition.add(calcOffsetVec(yDif * -parentOffset * this.getScale(), parentXRot, ourYRot));
        final Vec3 ourButt = parentButt.add(calcOffsetVec((yDif * -getBackOffset() - 0.5F * this.getBbWidth()) * this.getScale(), this.getXRot(), ourYRot));
        final Vec3 avg = new Vec3((parentButt.x + ourButt.x) / 2F, (parentButt.y + ourButt.y) / 2F, (parentButt.z + ourButt.z) / 2F);
        final double d0 = parentButt.x - ourButt.x;
        final double d2 = parentButt.z - ourButt.z;
        final double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        final double hgt = doHeight ? (getLowPartHeight(parentButt.x, parentButt.y, parentButt.z) + getHighPartHeight(ourButt.x, ourButt.y, ourButt.z)) : 0;
        if(Math.abs(prevHeight - hgt) > 0.2){
            prevHeight = hgt;
        }
        if(!isOpaqueBlockAt(parentFront.x,parentFront.y + 0.4F, parentFront.z) && Math.abs(prevHeight) > 1){
            prevHeight = 0;
        }
        final double partYDest = Mth.clamp(prevHeight, -0.4F, 0.4F);
        final float f = (float) (Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F;
        final float rawAngle = Mth.wrapDegrees((float) (-(Mth.atan2(partYDest, d3) * Mth.RAD_TO_DEG)));
        final float f2 = this.limitAngle(this.getXRot(), rawAngle, 10);
        this.setXRot(f2);
        this.entityData.set(BODY_XROT, f2);
        this.setYRot(f);
        this.yHeadRot = f;
        this.snapTo(avg.x, avg.y, avg.z, f, f2);
        return avg;
    }

    public float getXRot() {
        return this.entityData.get(BODY_XROT);
    }

    public double getLowPartHeight(double x, double yIn, double z) {
        if(isFluidAt(x, yIn, z)){
            return 0.0;
        }
        double checkAt = 0D;
        while (checkAt > -3D && !isOpaqueBlockAt(x,yIn + checkAt, z)) {
            checkAt -= 0.2D;
        }
        return checkAt;
    }

    public double getHighPartHeight(double x, double yIn, double z) {
        if(isFluidAt(x, yIn, z)){
            return 0.0;
        }
        double checkAt = 0D;
        while (checkAt <= 3) {
            if(isOpaqueBlockAt(x, yIn + checkAt, z)) {
                checkAt += 0.2D;
            }else{
                break;
            }
        }
        return checkAt;
    }

    public boolean isFluidAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            return !level().getFluidState(AMBlockPos.fromCoords(x, y, z)).isEmpty();
        }
    }

    public boolean isOpaqueBlockAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            final float f = 1F;
            final Vec3 vec3 = new Vec3(x, y, z);
            final AABB axisalignedbb = AABB.ofSize(vec3, f, 1.0E-6D, f);
            return this.level().getBlockStates(axisalignedbb).filter(Predicate.not(BlockBehaviour.BlockStateBase::isAir)).anyMatch((p_185969_) -> {
                final BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                return p_185969_.isSuffocating(this.level(), blockpos) && Shapes.joinIsNotEmpty(p_185969_.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisalignedbb), BooleanOp.AND);
            });
        }
    }

    public boolean canBreatheUnderwaterAM() {
        return true;
    }

    public float getBackOffset() {
        return 0.5F;
    }

    @Override
    public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
        if(parent.deathTime > 0){
            this.deathTime = parent.deathTime;
        }
        if(parent.hurtTime > 0){
            this.hurtTime = parent.hurtTime;
        }
    }
}